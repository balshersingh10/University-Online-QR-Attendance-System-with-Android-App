from rest_framework import serializers
from django.contrib.auth.models import User


class UserSerializer(serializers.Serializer):
    username = serializers.CharField()
    password = serializers.CharField()

class UserResponse(serializers.Serializer):
    flag = serializers.CharField()

class CourseSerializer(serializers.Serializer):
    username = serializers.CharField()

class CourseResponse(serializers.Serializer):
    courses = serializers.CharField()

class EnrollSerializer(serializers.Serializer):
    username = serializers.CharField()
    course_id = serializers.CharField()

class EnrollResponse(serializers.Serializer):
    message = serializers.CharField()

class QRSerializer(serializers.Serializer):
    username = serializers.CharField()
    qr =serializers.CharField()

class QRResponse(serializers.Serializer):
    message = serializers.CharField()

class SignUpSerializer(serializers.Serializer):
    username = serializers.CharField()
    password = serializers.CharField()
    fname = serializers.CharField()
    lname = serializers.CharField()
    email = serializers.CharField()

class SignUpResponse(serializers.Serializer):
    flag = serializers.CharField()

class AllCoursesResponse(serializers.Serializer):
    course_id = serializers.CharField()
    course_title = serializers.CharField()

class AttendanceSerializer(serializers.Serializer):
    username = serializers.CharField()
    course_id = serializers.CharField()

class AttendanceResponse(serializers.Serializer):
    total_classes = serializers.CharField()
    total_attended = serializers.CharField()

class DeregisterSerializer(serializers.Serializer):
    username = serializers.CharField()
    course_id = serializers.CharField()

class DeregisterResponse(serializers.Serializer):
    message = serializers.CharField()