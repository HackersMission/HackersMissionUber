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
		sig={
		    "_id" : "569a4bc16172250611f960c6",
		    "rytm" : 3,
		    "tone" : 3,
		    "tmbr" : 4,
		    "year" : 4,
		    "lang" : "日",
		    "url" : "http://m.qingting.fm/vchannels/137422/programs/3879480",
		    "mediaSubtitle" : "Hello( 日 本 )",
		    "mediaUrl" : "http://m.qingting.fm/m4a/569224bc7b28aa1a6d33e24e_4662924_64.m4a",
		    "mediaTitle" : "【卒業メモリーズ~サヨナラ、あなた】为了你而早起，为了你而改变，你是我的动力",
		    "mediaLengthStr" : "00:05:55",
		    "mediaImageUrl" : "http://pic.qingting.fm/2015/1209/20151209200925579.jpg",
		    "mediaLength" : 355
		}
		playlist.append(sig)
		# playlist.append("http://m.qingting.fm/vod/00/00/0000000000000000000026530084_24.m4a")
		return Response({"status":1, "data":playlist})