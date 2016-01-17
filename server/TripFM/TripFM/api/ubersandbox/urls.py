from django.conf.urls import include, url
from django.contrib import admin
from TripFM.api.ubersandbox import views

urlpatterns = [
    # Examples:
 	url(r"^sendrequest/$", views.SendRequest.as_view(), name="send_request"),
    url(r"^requestdetail/$", views.getRequestDetail.as_view(), name="test"),
    url(r"^accept/$", views.acceptRequest.as_view(), name="accept"),
    url(r"^arriving/$", views.arrivingRequest.as_view(), name="arriving"),
    url(r"^inprogress/$", views.inprogressRequest.as_view(), name="in_progress"),
    url(r"^completed/$", views.completedRequest.as_view(), name="completed"),
    url(r"^drivercancel/$", views.drivercanceledRequest.as_view(), name="driver_cancel"),
    url(r"^getestimatetime/$", views.getEstimateTime.as_view(), name="get_estimate_time"),
    url(r"^test/$", views.Test.as_view(), name="test"),
]
