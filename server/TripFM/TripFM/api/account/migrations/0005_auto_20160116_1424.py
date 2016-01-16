# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('account', '0004_auto_20160116_1123'),
    ]

    operations = [
        migrations.AlterField(
            model_name='accounttoken',
            name='access_token',
            field=models.CharField(max_length=2000),
        ),
        migrations.AlterField(
            model_name='accounttoken',
            name='expires_in',
            field=models.CharField(max_length=2000),
        ),
        migrations.AlterField(
            model_name='accounttoken',
            name='last_authenticated',
            field=models.CharField(max_length=2000),
        ),
        migrations.AlterField(
            model_name='accounttoken',
            name='refresh_token',
            field=models.CharField(max_length=2000),
        ),
        migrations.AlterField(
            model_name='accounttoken',
            name='token_type',
            field=models.CharField(max_length=2000),
        ),
    ]
