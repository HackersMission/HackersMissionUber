from django.conf.urls import include, url
from django.contrib import admin
from TripFM.api.recommend import views

urlpatterns = [
    # Examples:
    # url(r'^$', 'TripFM.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r"^recommend/$", views.AskToRecommend.as_view(), name="ask_to_recommend"),

]
