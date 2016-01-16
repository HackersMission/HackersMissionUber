from django.conf.urls import include, url
from django.contrib import admin
from TripFM.api.ubersandbox import views

urlpatterns = [
    # Examples:
 	url(r"^sendrequest/$", views.SendRequest.as_view(), name="send_request"),
    url(r"^cancelrequest/$", views.CancelRequest.as_view(), name="cancel_request"),
]
