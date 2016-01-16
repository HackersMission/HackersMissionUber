#coding=utf-8

from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from TripFM.api.account.models import AccountToken
from django.contrib import auth
from django.contrib.auth.models import User

import urllib2
import urllib
import httplib 
import json


def request_details(request_id,access_token):
	request_url="https://sandbox-api.uber.com/v1/requests/"
	request_url=request_url+request_id
	print request_url

	request = urllib2.Request(request_url)
	request.add_header('Authorization', 'Bearer '+access_token)
	request.add_header('Content-Type', 'application/json')

	response = urllib2.urlopen(request)
	ret = response.read()
	return ret


class SendRequest(APIView):
	def get(self, request, format=None):
		# request_url="https://sandbox-api.uber.com/v1/sandbox/products"
		request_url="https://sandbox-api.uber.com/v1/requests"
		# test_url="https://sandbox-api.uber.com/v1/products?latitude=37.7759792&longitude=-122.41823"
		start_latitude=37.334381
		start_longitude=-121.89432
		end_latitude=37.77703
		end_longitude=-122.419571

		try:
			user=User.objects.get(username="test1")
			account_token=AccountToken.objects.get(user=user)
			# print account_token.access_token
		except:
			return Response({"status":1, "info":"用户名或密码错误", "data":""})

		data={
			'start_latitude': start_latitude,
			'start_longitude': start_longitude,
			'end_latitude': end_latitude,
			'end_longitude': end_longitude
		}
		request = urllib2.Request(request_url,json.dumps(data))
		request.add_header('Authorization', 'Bearer '+account_token.access_token)
		request.add_header('Content-Type', 'application/json')

		response = urllib2.urlopen(request)
		ret = response.read()
		res_json=json.loads(ret)
		print res_json["request_id"]
		request_details(res_json["request_id"],account_token.access_token)
		return Response({"status":0, "info":res_json["request_id"], "data":""})



		

		


class CancelRequest(APIView):
	def get(self, request, format=None):

		return Response({"status":1, "info":"用户名或密码错误", "data":""})


class FinishOrder(APIView):
	def get(self, request, format=None):

		return Response({"status":1, "info":"用户名或密码错误", "data":""})