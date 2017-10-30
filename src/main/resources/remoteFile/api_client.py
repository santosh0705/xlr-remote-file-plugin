import io
import json
import ConfigParser
from java.util import Properties
from java.io import StringReader
from com.xebialabs.xlrelease.plugin.webhook import JsonPathResult
from com.xebialabs.xlrelease.plugin.webhook import XmlPathResult
import com.xebialabs.xlrelease.plugin.remotefile.RemoteFile as RemoteFile

class APIClient(object):

    def __init__(self, host, file_path):
        self.remote = RemoteFile(host)
        self.file_path = file_path

    def read_file(self):
        contents = self.remote.readFile(self.file_path)
        return contents

    def write_file(self, contents):
        self.remote.writeFile(self.file_path, contents)

    def get_var(self, file_type, v_path):
        contents = APIClient.read_file(self).encode().strip()
        file_type = file_type.upper()
        if file_type == "JSON":
            data = JsonPathResult(contents, v_path).get()
        elif file_type == "XML":
            data = XmlPathResult(contents, v_path).get()
        elif file_type == "INI":
            config = ConfigParser.SafeConfigParser(allow_no_value = True)
            config.readfp(io.BytesIO(contents))
            json_string = json.dumps(config._sections)
            data = JsonPathResult(json_string, v_path).get()
        elif file_type == "YAML":
            json_in_string = self.remote.yamlToJson(contents)
            data = JsonPathResult(json_in_string, v_path).get()
        elif file_type == "PROPERTIES":
            prop = Properties()
            prop.load(StringReader(contents))
            data = prop.getProperty(v_path)
        else:
            raise Exception('Given file type not supported.')
        return data
