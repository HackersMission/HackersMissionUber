# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('account', '0003_auto_20160116_1120'),
    ]

    operations = [
        migrations.AddField(
            model_name='accounttoken',
            name='expires_in',
            field=models.CharField(default='', max_length=500),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='accounttoken',
            name='last_authenticated',
            field=models.CharField(default='', max_length=500),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='accounttoken',
            name='token_type',
            field=models.CharField(default='', max_length=500),
            preserve_default=False,
        ),
    ]
