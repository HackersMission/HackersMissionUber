#!/bin/sh

echo "start first trip send request"
curl http://192.168.10.222:8000/api/ubersandbox/sendrequest/?username=test1\&seq=1
read num
echo "accept"
curl http://192.168.10.222:8000/api/ubersandbox/accept/?username=test1
read num
echo "arriving"
curl http://192.168.10.222:8000/api/ubersandbox/arriving/?username=test1
read num
echo "inprogress"
curl http://192.168.10.222:8000/api/ubersandbox/inprogress/?username=test1
read num
echo "completed"
curl http://192.168.10.222:8000/api/ubersandbox/completed/?username=test1
read num
echo "start second trip send request"
curl http://192.168.10.222:8000/api/ubersandbox/sendrequest/?username=test1\&seq=0
read num
echo "accept"
curl http://192.168.10.222:8000/api/ubersandbox/accept/?username=test1
read num
echo "arriving"
curl http://192.168.10.222:8000/api/ubersandbox/arriving/?username=test1
read num
echo "inprogress"
curl http://192.168.10.222:8000/api/ubersandbox/inprogress/?username=test1
read num
echo "completed"
curl http://192.168.10.222:8000/api/ubersandbox/completed/?username=test1
read num
echo "start third trip send request"
curl http://192.168.10.222:8000/api/ubersandbox/sendrequest/?username=test1\&seq=2
read num
echo "accept"
curl http://192.168.10.222:8000/api/ubersandbox/accept/?username=test1
read num
echo "arriving"
curl http://192.168.10.222:8000/api/ubersandbox/arriving/?username=test1
read num
echo "inprogress"
curl http://192.168.10.222:8000/api/ubersandbox/inprogress/?username=test1
read num
echo "completed"
curl http://192.168.10.222:8000/api/ubersandbox/completed/?username=test1


