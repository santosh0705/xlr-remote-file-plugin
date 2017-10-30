from remoteFile.api_client import APIClient

host = task.pythonScript.getProperty("host")
file_path = task.pythonScript.getProperty("filePath")
client = APIClient(host, file_path)

contents = task.pythonScript.getProperty("contents")
client.write_file(contents)
