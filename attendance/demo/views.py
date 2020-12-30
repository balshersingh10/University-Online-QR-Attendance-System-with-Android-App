from django.shortcuts import render
from django.contrib.auth.models import User
from django.http import HttpResponse

# Create your views here.


def index(request):

    user = User.objects.create_user('john', 'lennon@thebeatles.com', 'johnpassword')
    user.save()
    return HttpResponse("Hello, world. You're at the polls index.")
