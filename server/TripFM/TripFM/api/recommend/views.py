#coding=utf-8

from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from TripFM.api.account.models import AccountToken
from TripFM.api.ubersandbox.views import get_request_for_recommend,get_eta_by_startpoint
from django.contrib import auth
from django.contrib.auth.models import User
from bosonnlp import BosonNLP

import urllib2
import urllib
import httplib 
import json
from urllib import quote
from urllib import urlencode


def getKeyWords(command):
	nlp = BosonNLP("IrtCRUKX.4360.giOuq49VR3V-")
	r = nlp.extract_keywords(command, top_k=3)
	l = []
	for k,v in r:
		v = v.encode('utf8')
		l.append(v)
	return l

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
		duration=0
		request_detail = json.loads(get_request_for_recommend("test1"))
		status = request_detail["status"]
		print request_detail
		# if status=='accepted':
		# 	pickup_eta=request_detail['pickup']['eta']
		# 	print pickup_eta
		# 	des_lati=request_detail['destination']['latitude']
		# 	loc_lati=request_detail['location']['latitude']
		# 	des_long=request_detail['destination']['longitude']
		# 	loc_long=request_detail['location']['longitude']
		# elif status=='arriving':
		# 	pickup_eta=request_detail['pickup']['eta']
		# 	print pickup_eta
		# 	eta=request_detail['eta']
		# 	print eta
		# 	des_lati=request_detail['destination']['latitude']
		# 	loc_lati=request_detail['location']['latitude']
		# 	des_long=request_detail['destination']['longitude']
		# 	loc_long=request_detail['location']['longitude']
		# elif status=='in_progress':
		# 	des_lati=request_detail['destination']['latitude']
		# 	loc_lati=request_detail['location']['latitude']
		# 	des_long=request_detail['destination']['longitude']
		# 	loc_long=request_detail['location']['longitude']
		# print abs(des_long)	
		if status=='accepted' or status=='arriving' or status=='in_progress':
			des_lati=request_detail['destination']['latitude']
			loc_lati=request_detail['location']['latitude']
			des_long=request_detail['destination']['longitude']
			loc_long=request_detail['location']['longitude']
		if des_lati!=0:
			# duration=get_eta_by_startpoint(str("%.4f"%abs(loc_lati)),str("%.4f"%abs(loc_long)),str("%.4f"%abs(des_lati)),str("%.4f"%abs(des_long)),'test1')
			duration=json.loads(get_eta_by_startpoint(str(abs(loc_lati)),str(abs(loc_long)),str(abs(des_lati)),str(abs(des_long)),'test1'))
			duration=duration['prices'][0]['duration']
		return Response({"status":1, "info":"推荐成功", "data":duration})



class getPlayList(APIView):
	def get(self, request, format=None):
		request_url="http://192.168.11.39:2088"

		des_lati=0
		loc_lati=0
		des_long=0
		loc_long=0
		duration=0
		request_detail = json.loads(get_request_for_recommend("test1"))
		status = request_detail["status"]
		print request_detail
		if status=='accepted' or status=='arriving' or status=='in_progress':
			des_lati=request_detail['destination']['latitude']
			loc_lati=request_detail['location']['latitude']
			des_long=request_detail['destination']['longitude']
			loc_long=request_detail['location']['longitude']
		if des_lati!=0:
			duration=json.loads(get_eta_by_startpoint(str(abs(loc_lati)),str(abs(loc_long)),str(abs(des_lati)),str(abs(des_long)),'test1'))
			duration=duration['prices'][0]['duration']

		try:
			command=request.query_params["command"]
			print command
		except:
			command=""
		# command=json.dumps(getKeyWords(command))
		print command
		key_list=getKeyWords(command)
		key='['
		count=0
		for l in key_list:
			key=key+'"'+l+'"'
			if count<len(key_list)-1:
				key=key+','
			count=count+1
		key=key+']'
		command=key
		command=quote(command)
		print command

		try:
			age=request.query_params["age"]
		except:
			age=""
		try:
			gender=request.query_params["gender"]
		except:
			gender=""
		try:
			attractive=request.query_params["attractive"]
		except:
			attractive=""

		request_url=request_url+'?start_latitude='+str(abs(loc_lati))+'&start_longitude='\
		+str(abs(loc_long))+'&end_latitude='+str(abs(des_lati))+'&end_longitude='\
		+str(abs(des_long))+'&duration='+str(duration)+'&command='+command+'&username='+'test1'\
		+'&age='+str(age)+'&gender='+str(gender)+'&attractive='+str(attractive)

		print request_url
		request = urllib2.Request(request_url)
		response = urllib2.urlopen(request)
		ret = response.read()
		print ret
		return Response({"status":1, "data":ret})



class Operate(APIView):
	def get(self, request, format=None):
		operation=request.query_params["operation"]
		cur_uri=request.query_params["url"]
		username=request.query_params["username"]

		request_url="http://192.168.11.39:2088/"
		request_url=request_url+'?callBack='+str(operation)+'&mediaUrl='+cur_uri+'&userName='+username
		print request_url

		request = urllib2.Request(request_url)
		response = urllib2.urlopen(request)
		ret = response.read()
		print ret
		# key_list=getKeyWords('那英是个傻逼')
		# key='['
		# count=0
		# for l in key_list:
		# 	key=key+'"'+l+'"'
		# 	if count<len(key_list)-1:
		# 		key=key+','
		# 	count=count+1
		# key=key+']'
		# print key
		return Response({"status":1, "data":""})
