# delete_sftp_file.py

import os
import paramiko
import re

def delete_file_from_sftp(server, port, username, password, artifact_id):
    try:
        transport = paramiko.Transport((server, int(port)))
        transport.connect(username=username, password=password)

        sftp = paramiko.SFTPClient.from_transport(transport)

        directory = '/plugins/'
        files = sftp.listdir(directory)
        for file in files:
            print(f"checking {file}")
            if file.startswith(artifact_id) and file.endswith('.jar'):
                print(f"Deleting {file}")
                sftp.remove(os.path.join(directory, file))
            else:
                print(f"Skipping {file}")

        sftp.close()
        transport.close()
        print("File deletion completed successfully.")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    SFTP_SERVER = os.getenv('SFTP_SERVER')
    SFTP_PORT = os.getenv('SFTP_PORT')
    SFTP_USERNAME = os.getenv('SFTP_USERNAME')
    SFTP_PASSWORD = os.getenv('SFTP_PASSWORD')
    ARTIFACT_ID = os.getenv('ARTIFACT_ID')

    print(ARTIFACT_ID)

    delete_file_from_sftp(SFTP_SERVER, SFTP_PORT, SFTP_USERNAME, SFTP_PASSWORD, ARTIFACT_ID)