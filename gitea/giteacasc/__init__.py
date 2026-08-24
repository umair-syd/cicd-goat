import click
import yaml
from giteacasc.gitea import Gitea
import os
import logging
import http.client
import secrets
import string

http.client.HTTPConnection.debuglevel = 5
logging.basicConfig(level=logging.DEBUG)


def generate_random_password(length=16):
    """Generate a cryptographically secure random password."""
    alphabet = string.ascii_letters + string.digits + string.punctuation
    return ''.join(secrets.choice(alphabet) for _ in range(length))


@click.command()
@click.argument('path', type=str)
@click.option('-u', '--username', 'admin_username', help='Admin username.')
@click.option('-p', '--password', '****word', help='Admin password.')
def giteacasc(path, admin_username, ****word):
    with open(path, 'r') as y:
        config = yaml.safe_load(y.read())
    project_dir = os.path.dirname(os.path.abspath(__file__))
    os.environ['GIT_ASKPASS'] = os.path.join(project_dir, 'askpass.py')
    os.environ['GIT_USERNAME'] = admin_username
    os.environ['GIT_PASSWORD'] = ****word
    g = Gitea(admin_username, ****word)
    if Gitea.YAML_USERS in config:
        for username in config[Gitea.YAML_USERS]:
            user_config = config[Gitea.YAML_USERS][username].copy()
            # If password is not provided or is a placeholder, generate a random one
            if 'password' not in user_config or not user_config['password'] or user_config['password'].startswith('${'):
                user_config['password'] = generate_random_password()
                logging.info(f"Generated random password for user {username}")
            # Ensure must_change_password is True if not explicitly set to False
            if 'must_change_password' not in user_config:
                user_config['must_change_password'] = True
            g.create_user(username, **user_config)
    if Gitea.YAML_ORGS in config:
        for org_name in config[Gitea.YAML_ORGS]:
            g.create_org(admin_username, org_name, **config[Gitea.YAML_ORGS][org_name])


