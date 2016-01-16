from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class Order(models.Model):
	user = models.ForeignKey(User)
	request_id = models.CharField(max_length=500)
	
