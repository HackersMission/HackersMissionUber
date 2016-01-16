# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('account', '0002_auto_20160116_0903'),
    ]

    operations = [
        migrations.AlterField(
            model_name='accounttoken',
            name='access_token',
            field=models.CharField(max_length=500),
        ),
        migrations.AlterField(
            model_name='accounttoken',
            name='refresh_token',
            field=models.CharField(max_length=500),
        ),
    ]
