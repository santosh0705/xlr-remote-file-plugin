from remoteFile.api_client import APIClient

host = task.pythonScript.getProperty("host")
file_path = task.pythonScript.getProperty('filePath')
client = APIClient(host, file_path)

file_type = task.pythonScript.getProperty('fileType')
if file_type == None:
    raise Exception('Please provide file type.')

v_path1 = task.pythonScript.getProperty('vPath1')
v_path2 = task.pythonScript.getProperty('vPath2')
v_path3 = task.pythonScript.getProperty('vPath3')

if v_path1 != None and v_path1.strip() != '':
    result1 = client.get_var(file_type, v_path1)
if v_path2 != None and v_path2.strip() != '':
    result2 = client.get_var(file_type, v_path2)
if v_path3 != None and v_path3.strip() != '':
    result3 = client.get_var(file_type, v_path3)
