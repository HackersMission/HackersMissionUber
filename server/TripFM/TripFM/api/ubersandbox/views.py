#coding=utf-8

from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authtoken.models import Token
from TripFM.api.account.models import AccountToken
from TripFM.api.ubersandbox.models import Order
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


def get_request_for_recommend(username):
	try:
		user=User.objects.get(username=username)
		account_token=AccountToken.objects.get(user=user)
		try:
			order = Order.objects.get(user=user)
			data = request_details(order.request_id,account_token.access_token)
			return data
		except:
			return '{"status":"no order"}'
	except:
		return '{"status":"no user"}'


def get_eta_by_startpoint(loc_lati,loc_long,des_lati,des_long,username):
	try:
		user=User.objects.get(username=username)
		account_token=AccountToken.objects.get(user=user)
	except:
		return '{"status":"no user"}'
	# request_url="https://sandbox-api.uber.com/v1/estimates/time?"
	request_url="https://sandbox-api.uber.com/v1/estimates/price?"
	request_url=request_url+"start_latitude="+loc_lati+"&start_longitude="+loc_long+"&end_latitude="+des_lati+"&end_longitude="+des_long
	print request_url

	request = urllib2.Request(request_url)
	request.add_header('Authorization', 'Bearer '+account_token.access_token)
	request.add_header('Content-Type', 'application/json')

	response = urllib2.urlopen(request)
	ret = response.read()
	return ret





class SendRequest(APIView):
	def get(self, request, format=None):
		# request_url="https://sandbox-api.uber.com/v1/sandbox/products"
		request_url="https://sandbox-api.uber.com/v1/requests"
		# test_url="https://sandbox-api.uber.com/v1/products?latitude=37.7759792&longitude=-122.41823"
		start_latitude=39.918353
		start_longitude=116.464682
		end_latitude=39.998325 
		end_longitude=-116.320666

		try:
			username = request.query_params["username"]
			user=User.objects.get(username=username)
			account_token=AccountToken.objects.get(user=user)
			print account_token.access_token
		except:
			return Response({"status":1, "info":"无该用户", "data":""})

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
		print res_json

		try:
			order = Order.objects.get(user=user)
		except:
			order = Order()
		order.user=user
		order.request_id=res_json["request_id"]
		order.save()
		# request_details(res_json["request_id"],account_token.access_token)
		return Response({"status":0, "info":res_json["request_id"], "data":""})



		
class Test(APIView):
	def get(self, request, format=None):
		# order = Order.objects.all()
		# for o in order:
		# 	print o.user
		# 	# if o.user.username=="hou":
		# 	# 	o.delete()
		# 	print o.request_id
		# return Response({"status":1, "info":len(order), "data":""})
		# print get_eta_by_startpoint('39.918353','116.464682','39.998325','116.320666','test1')
		print get_eta_by_startpoint('39.9184','116.4647','39.9982','116.3208','test1')
		return Response({"status":1, "info":"", "data":""})
		

class getRequestDetail(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		try:
			user=User.objects.get(username=username)
			account_token=AccountToken.objects.get(user=user)
			try:
				order = Order.objects.get(user=user)
				data = request_details(order.request_id,account_token.access_token)
				return Response({"status":1, "info":"获得成功", "data":data})
			except:
				return Response({"status":1, "info":"该用户无订单", "data":""})
		except:
			return Response({"status":1, "info":"无该用户", "data":""})


class acceptRequest(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		try:
			user=User.objects.get(username=username)
			account_token=AccountToken.objects.get(user=user)
		except:
			return Response({"status":1, "info":"无该用户", "data":""})
		try:
			request_url="https://sandbox-api.uber.com/v1/sandbox/requests/" 
			order = Order.objects.get(user=user)
			request_url=request_url+order.request_id
		except:
			return Response({"status":1, "info":"该用户无订单", "data":""})

		data={
			'status': 'accepted'
		}
		request = urllib2.Request(request_url,json.dumps(data))
		request.add_header('Authorization', 'Bearer '+account_token.access_token)
		request.add_header('Content-Type', 'application/json')
		request.get_method = lambda:"PUT"

		response = urllib2.urlopen(request)
		ret = response.read()
		# res_json=json.loads(ret)
		print ret
		return Response({"status":1, "info":"accept订单成功", "data":data})


class arrivingRequest(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		try:
			user=User.objects.get(username=username)
			account_token=AccountToken.objects.get(user=user)
		except:
			return Response({"status":1, "info":"无该用户", "data":""})
		try:
			request_url="https://sandbox-api.uber.com/v1/sandbox/requests/" 
			order = Order.objects.get(user=user)
			request_url=request_url+order.request_id
		except:
			return Response({"status":1, "info":"该用户无订单", "data":""})

		data={
			'status': 'arriving'
		}
		request = urllib2.Request(request_url,json.dumps(data))
		request.add_header('Authorization', 'Bearer '+account_token.access_token)
		request.add_header('Content-Type', 'application/json')
		request.get_method = lambda:"PUT"

		response = urllib2.urlopen(request)
		ret = response.read()
		# res_json=json.loads(ret)
		print ret
		return Response({"status":1, "info":"汽车已经arriving", "data":data})


class inprogressRequest(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		try:
			user=User.objects.get(username=username)
			account_token=AccountToken.objects.get(user=user)
		except:
			return Response({"status":1, "info":"无该用户", "data":""})
		try:
			request_url="https://sandbox-api.uber.com/v1/sandbox/requests/" 
			order = Order.objects.get(user=user)
			request_url=request_url+order.request_id
		except:
			return Response({"status":1, "info":"该用户无订单", "data":""})

		data={
			'status': 'in_progress'
		}
		request = urllib2.Request(request_url,json.dumps(data))
		request.add_header('Authorization', 'Bearer '+account_token.access_token)
		request.add_header('Content-Type', 'application/json')
		request.get_method = lambda:"PUT"

		response = urllib2.urlopen(request)
		ret = response.read()
		# res_json=json.loads(ret)
		print ret
		return Response({"status":1, "info":"旅程已经开始", "data":data})


class completedRequest(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		try:
			user=User.objects.get(username=username)
			account_token=AccountToken.objects.get(user=user)
		except:
			return Response({"status":1, "info":"无该用户", "data":""})
		try:
			request_url="https://sandbox-api.uber.com/v1/sandbox/requests/" 
			order = Order.objects.get(user=user)
			request_url=request_url+order.request_id
		except:
			return Response({"status":1, "info":"该用户无订单", "data":""})

		data={
			'status': 'completed'
		}
		request = urllib2.Request(request_url,json.dumps(data))
		request.add_header('Authorization', 'Bearer '+account_token.access_token)
		request.add_header('Content-Type', 'application/json')
		request.get_method = lambda:"PUT"

		response = urllib2.urlopen(request)
		ret = response.read()
		# res_json=json.loads(ret)
		print ret
		return Response({"status":1, "info":"订单已经完成", "data":data})




class drivercanceledRequest(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		try:
			user=User.objects.get(username=username)
			account_token=AccountToken.objects.get(user=user)
		except:
			return Response({"status":1, "info":"无该用户", "data":""})
		try:
			request_url="https://sandbox-api.uber.com/v1/sandbox/requests/" 
			order = Order.objects.get(user=user)
			request_url=request_url+order.request_id
		except:
			return Response({"status":1, "info":"该用户无订单", "data":""})

		data={
			'status': 'driver_canceled'
		}
		request = urllib2.Request(request_url,json.dumps(data))
		request.add_header('Authorization', 'Bearer '+account_token.access_token)
		request.add_header('Content-Type', 'application/json')
		request.get_method = lambda:"PUT"

		response = urllib2.urlopen(request)
		ret = response.read()
		# res_json=json.loads(ret)
		print ret
		return Response({"status":1, "info":"司机已经取消了订单", "data":data})


class getEstimateTime(APIView):
	def get(self, request, format=None):
		username = request.query_params["username"]
		des_lati=0
		loc_lati=0
		des_long=0
		loc_long=0
		duration=0
		request_detail = json.loads(get_request_for_recommend(username))
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
		return Response({"status":0, "data":duration})
		