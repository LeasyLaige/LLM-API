from rest_framework import serializers


class PromptSerializer(serializers.Serializer):
    prompt = serializers.CharField(required=True, allow_blank=False)


class ResultSerializer(serializers.Serializer):
    response = serializers.CharField()

