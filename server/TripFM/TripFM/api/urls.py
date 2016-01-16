from django.conf.urls import include, url
from django.contrib import admin

urlpatterns = [
    # Examples:
    # url(r'^$', 'TripFM.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r"^account/", include("TripFM.api.account.urls")),
    url(r"^recommend/", include("TripFM.api.recommend.urls")),
    url(r"^ubersandbox/", include("TripFM.api.ubersandbox.urls")),
]
