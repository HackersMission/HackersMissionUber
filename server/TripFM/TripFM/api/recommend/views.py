#coding=utf-8

from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from TripFM.api.account.models import AccountToken
from django.contrib import auth
from django.contrib.auth.models import User


class AskToRecommend(APIView):
	def get(self, request, format=None):
		command = request.query_params["command"]
		heart_rate = request.query_params["heart_rate"]
		steps = request.query_params["steps"]
		start_time = request.query_params["start_time"]
		end_time = request.query_params["end_time"]
		location = request.query_params["location"]
		user_token = request.query_params["user_token"]

		return Response({"status":1, "info":"推荐成功", "data":""})



class getPlayList(APIView):
	def get(self, request, format=None):
		playlist=[]
		playlist.append("http://m.qingting.fm/vod/00/00/0000000000000000000026530084_24.m4a")
		return Response({"status":1, "info":playlist, "data":""})