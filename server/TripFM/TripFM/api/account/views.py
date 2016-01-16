#coding=utf-8

from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from TripFM.api.account.models import AccountToken
from django.contrib import auth
from django.contrib.auth.models import User

# Create your views here.

def loginUser(user):
	try :
		token = Token.objects.get(user=user).delete()
	except:
		pass			
	token = Token.objects.create(user=user)
	return Response({"status":0,"info":"","data":{"token":token.key}})


class Login(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		password = request.query_params["password"]
		# password = request.data["password"]
		try:
			user = auth.authenticate(username=username, password=password)
			if user is not None:
				return loginUser(user)
			else:
				return Response({"status":1, "info":"用户名或密码错误", "data":""})
		except:
			return Response({"status":1, "info":"用户名或密码错误", "data":""})


class Register(APIView):
	def get(self, request, format=None):
		user = User()
		user.username = request.query_params["username"]
		user.set_password(request.query_params["password"])
		try:
			user.save()
			return Response({"status":0, "info":"注册成功", "data":""})
		except:
			return Response({"status":1, "info":"该用户已注册", "data":""})


class AddToken(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		access_token = request.query_params["access_token"]
		refresh_token = request.query_params["refresh_token"]
		token_type = request.query_params["token_type"]
		last_authenticated = request.query_params["last_authenticated"]
		expires_in = request.query_params["expires_in"]
		try:
			user=User.objects.get(username=username)
			token = AccountToken()
			token.user=user
			token.access_token=access_token
			token.refresh_token=refresh_token
			token.save
			return Response({"status":0, "info":"token更新成功", "data":""})
		except:
			return Response({"status":1, "info":"该用户未注册", "data":""})


class CallBack(APIView):
	def get(self, request, format=None):
		# user = User.objects.all()
		# token = AccountToken.objects.all()
		# return Response({"status":1, "info":len(token), "data":""})
		try:
			code = request.query_params["code"]
			print request
			return Response({"status":0, "info":code, "data":""})
		except:
			print "wrong"
			return Response({"status":1, "info":"wrong", "data":""})



class Test(APIView):
	def get(self, request, format=None):
		# user = User.objects.all()
		# token = AccountToken.objects.all()
		# return Response({"status":1, "info":len(token), "data":""})
		try:
			code = request.query_params["code"]
			print code
			return Response({"status":0, "info":code, "data":""})
		except:
			print "wrong"
			return Response({"status":1, "info":"wrong", "data":""})
			