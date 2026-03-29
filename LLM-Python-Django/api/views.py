"""
Views for the /api/generate/ endpoint.

Integrates with Hugging Face Inference Providers via the openai Python package.
"""

import logging
import os

import openai
from rest_framework import status
from rest_framework.request import Request
from rest_framework.response import Response
from rest_framework.views import APIView

from .serializers import PromptSerializer, ResultSerializer

logger = logging.getLogger(__name__)

# --------------- OpenAI-compatible HF client ---------------
HF_TOKEN = os.getenv("HF_TOKEN", "")
MODEL = "meta-llama/Meta-Llama-3-8B-Instruct"

client = openai.OpenAI(
    base_url="https://router.huggingface.co/v1",
    api_key=HF_TOKEN,
    timeout=60.0,
)


class GenerateView(APIView):
    """Accept a prompt and return a generated response from the LLM."""

    def post(self, request: Request) -> Response:
        # --- validate input ---
        serializer = PromptSerializer(data=request.data)
        if not serializer.is_valid():
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

        prompt: str = serializer.validated_data["prompt"]

        # --- call the LLM ---
        try:
            completion = client.chat.completions.create(
                model=MODEL,
                messages=[
                    {
                        "role": "system",
                        "content": "You are a helpful assistant.",
                    },
                    {
                        "role": "user",
                        "content": prompt,
                    },
                ],
            )
            generated_text = completion.choices[0].message.content

        except openai.APITimeoutError:
            logger.exception("Hugging Face API timed out")
            return Response(
                {"error": "The LLM service timed out. Please try again later."},
                status=status.HTTP_504_GATEWAY_TIMEOUT,
            )
        except openai.APIError as exc:
            logger.exception("Hugging Face API error: %s", exc)
            return Response(
                {"error": f"LLM service error: {exc}"},
                status=status.HTTP_502_BAD_GATEWAY,
            )
        except Exception as exc:  # noqa: BLE001
            logger.exception("Unexpected error while calling the LLM: %s", exc)
            return Response(
                {"error": "An unexpected error occurred."},
                status=status.HTTP_500_INTERNAL_SERVER_ERROR,
            )

        # --- return the result ---
        result_serializer = ResultSerializer(data={"response": generated_text})
        result_serializer.is_valid(raise_exception=True)
        return Response(result_serializer.validated_data, status=status.HTTP_200_OK)

