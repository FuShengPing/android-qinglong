var API_config = "api/configs/config"
var API_script = ""

var _host = "";
var _authorization = "";

var editor;
var _path;
var _filename;

//script
var script_content;
var script_isValid;

//config
var config_content;
var config_isValid;

function initConfig(host, authorization) {
    _host = host;//主址
    _authorization = authorization;//授权码

    getConfig()
}

function getConfig() {
    url = _host + "api/configs/config.sh"
    headers = { "Authorization": _authorization }

    axios.get(url, { headers: headers })
        .then(function (response) {
            body = response.data
            if (body['code'] == 200) {
                config_content = body['data'];
                config_isValid = true;
                setCode(body['data']);
            } else if (body['code'] == 401) {
                config_isValid = false;
                setCode("无效会话")
            } else {
                config_isValid = false;
                setCode("请求异常")
            }
        }).catch(function (error) {
            config_isValid = false;
            setCode("请求异常：" + error['message'])
        })
}

function saveConfig() {
    if (!config_isValid) {
        return
    }

    url = _host + "api/configs/save"
    headers = { "Authorization": _authorization, "Content-Type": "application/json;charset=UTF-8" }
    body = { "content": editor.getValue(), "name": "config.sh" }

    axios.post(url, body, { headers: headers })
        .then(function (response) {
            body = response.data
            if (body['code'] == 200) {
                config_isValid = true;
                config_content = editor.getValue()
            } else if (body['code'] == 401) {
                config_isValid = false;
                setCode("无效会话")
            } else {
                config_isValid = false;
                setCode("请求异常")
            }
        }).catch(function (error) {
            setCode("请求异常：" + error['message'])
        })
}

function refreshConfig() {
    getConfig()
}

function backConfig() {
    if (config_content) {
        setCode(config_content)
    }
}




function initOfLog(host, authorization, path) {
    _host = host;//主址
    _authorization = authorization;//授权码
}

function initOfScript(host, authorization, filename, path) {
    _host = host;//主址
    _authorization = authorization;//授权码
    _filename = filename;//文件名
    _path = path;//文件路径

    getScript()
}

function getScript() {
    url = _host + "api/scripts/" + _filename + "?path=" + _path;
    headers = { "Authorization": _authorization }

    axios.get(url, { headers: headers })
        .then(function (response) {
            body = response.data
            if (body['code'] == 200) {
                script_content = body['data'];
                script_isValid = true;
                setCode(body['data']);

            } else if (body['code'] == 401) {
                script_isValid = false;
                setCode("无效会话")
            } else {
                script_isValid = false;
                setCode("请求异常")
            }
        }).catch(function (error) {
            setCode("请求异常：" + error.response['statusText'])
        })
}

function saveScript() {
    if (!script_isValid) {
        return
    }

    url = _host + "api/scripts"
    headers = { "Authorization": _authorization, "Content-Type": "application/json;charset=UTF-8" }
    body = { "content": editor.getValue(), "filename": _filename, "path": path }

    axios.put(url, body, { headers: headers })
        .then(function (response) {
            body = response.data
            if (body['code'] == 200) {
                script_content = editor.getValue()
            } else if (body['code'] == 401) {
                script_isValid = false;
                setCode("无效会话")
            } else {
                script_isValid = false;
                setCode("请求异常")
            }
        }).catch(function (error) {
            script_isValid = false;
            setCode("请求异常：" + error['message'])
        })

}

function initEditor() {
    editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: {
            name: "python",
            version: 3,
            singleLineStringErrors: false
        },
        theme: "solarized",//主题风格
        lineNumbers: true,//显示行号
        indentUnit: 4,
        matchBrackets: true,
        readOnly: true,//只读
        lineWrapping: false,//换行
    });
    editor.refresh()
}

function setCode(code) {
    if (code == editor.getValue()) {
        return
    }
    editor.setValue(code)
    editor.refresh()
}

function setEditable(editable) {
    editor.options.readOnly = !editable
}




function refresh() {

}