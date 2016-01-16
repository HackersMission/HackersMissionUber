# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('account', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='accounttoken',
            old_name='token',
            new_name='access_token',
        ),
        migrations.AddField(
            model_name='accounttoken',
            name='refresh_token',
            field=models.CharField(default='', max_length=50),
            preserve_default=False,
        ),
    ]
