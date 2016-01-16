from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class AccountToken(models.Model):
	user = models.ForeignKey(User)
	access_token = models.CharField(max_length=500)
	refresh_token = models.CharField(max_length=500)
	token_type = models.CharField(max_length=500)
	last_authenticated = models.CharField(max_length=500)
	expires_in = models.CharField(max_length=500)