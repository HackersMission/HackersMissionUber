from django.conf.urls import include, url
from django.contrib import admin
from TripFM.api.recommend import views

urlpatterns = [
    # Examples:
    # url(r'^$', 'TripFM.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r"^recommend/$", views.AskToRecommend.as_view(), name="ask_to_recommend"),
    url(r"^getplaylist/$", views.getPlayList.as_view(), name="get_playlist"),
    url(r"^operate/$", views.Operate.as_view(), name="get_playlist"),
]
