#coding=utf-8

from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from TripFM.api.account.models import AccountToken
from TripFM.api.ubersandbox.views import get_request_for_recommend
from django.contrib import auth
from django.contrib.auth.models import User

import urllib2
import urllib
import httplib 
import json


class AskToRecommend(APIView):
	def get(self, request, format=None):
		# command = request.query_params["command"]
		# heart_rate = request.query_params["heart_rate"]
		# steps = request.query_params["steps"]
		# start_time = request.query_params["start_time"]
		# end_time = request.query_params["end_time"]
		# location = request.query_params["location"]
		# user_token = request.query_params["user_token"]

		des_lati=0
		loc_lati=0
		des_long=0
		loc_long=0
		request_detail = json.loads(get_request_for_recommend("test1"))
		status = request_detail["status"]
		print request_detail
		if status=='accepted':
			pickup_eta=request_detail['pickup']['eta']
			print pickup_eta
			des_lati=request_detail['destination']['latitude']
			loc_lati=request_detail['location']['latitude']
			des_long=request_detail['destination']['longitude']
			loc_long=request_detail['location']['longitude']
		elif status=='arriving':
			pickup_eta=request_detail['pickup']['eta']
			print pickup_eta
			eta=request_detail['eta']
			print eta
			des_lati=request_detail['destination']['latitude']
			loc_lati=request_detail['location']['latitude']
			des_long=request_detail['destination']['longitude']
			loc_long=request_detail['location']['longitude']
		elif status=='in_progress':
			des_lati=request_detail['destination']['latitude']
			loc_lati=request_detail['location']['latitude']
			des_long=request_detail['destination']['longitude']
			loc_long=request_detail['location']['longitude']
			print des_lati
		return Response({"status":1, "info":"推荐成功", "data":""})



class getPlayList(APIView):
	def get(self, request, format=None):
		request_url="http://192.168.11.39:2088"

		request = urllib2.Request(request_url)
		response = urllib2.urlopen(request)
		ret = response.read()
		print ret
		playlist=[]
		# sig={
		#     "_id" : "569a4bc16172250611f960c6",
		#     "rytm" : 3,
		#     "tone" : 3,
		#     "tmbr" : 4,
		#     "year" : 4,
		#     "lang" : "日",
		#     "url" : "http://m.qingting.fm/vchannels/137422/programs/3879480",
		#     "mediaSubtitle" : "Hello( 日 本 )",
		#     "mediaUrl" : "http://m.qingting.fm/m4a/569224bc7b28aa1a6d33e24e_4662924_64.m4a",
		#     "mediaTitle" : "【卒業メモリーズ~サヨナラ、あなた】为了你而早起，为了你而改变，你是我的动力",
		#     "mediaLengthStr" : "00:05:55",
		#     "mediaImageUrl" : "http://pic.qingting.fm/2015/1209/20151209200925579.jpg",
		#     "mediaLength" : 355
		# }
		# playlist.append(sig)
		# playlist.append("http://m.qingting.fm/vod/00/00/0000000000000000000026530084_24.m4a")
		return Response({"status":1, "data":ret})