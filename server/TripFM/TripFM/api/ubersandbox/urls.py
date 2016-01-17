from django.conf.urls import include, url
from django.contrib import admin
from TripFM.api.ubersandbox import views

urlpatterns = [
    # Examples:
 	url(r"^sendrequest/$", views.SendRequest.as_view(), name="send_request"),
    url(r"^requestdetail/$", views.getRequestDetail.as_view(), name="test"),
    url(r"^accept/$", views.acceptRequest.as_view(), name="test"),
    url(r"^arriving/$", views.arrivingRequest.as_view(), name="test"),
    url(r"^inprogress/$", views.inprogressRequest.as_view(), name="test"),
    url(r"^completed/$", views.completedRequest.as_view(), name="test"),
    url(r"^drivercancel/$", views.drivercanceledRequest.as_view(), name="test"),
    url(r"^test/$", views.Test.as_view(), name="test"),
]
