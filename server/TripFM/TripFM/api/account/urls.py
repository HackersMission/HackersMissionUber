from django.conf.urls import include, url
from django.contrib import admin
from TripFM.api.account import views

urlpatterns = [
    # Examples:
    # url(r'^$', 'TripFM.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r"^register/$", views.Register.as_view(), name="account_register"),
    url(r"^login/$", views.Login.as_view(), name="account_login"),
    url(r"^test/$", views.Test.as_view(), name="test"),
]
