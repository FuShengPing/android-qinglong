var editor = null

function init() {
    editor = CodeMirror.fromTextArea(document.getElementById("code"), {
        mode: "python",//采用python语法高亮
        theme: "solarized",//主题风格
        lineWrapping: false,//超出不换行
        lineNumbers: true,//显示行号
        fixedGutter:true,//固定行号视图
        readOnly: true,//只读
        maxHighlightLength: 200,//单行高亮显示的内容长度
        indentUnit: 4,//缩进大小
    });
}

function setCode(code) {
    code = decodeURIComponent(code)
    if (code == editor.getValue()) {
        return
    }
    editor.setValue(code)
    editor.save()
}

function getContent(){
     editor.save()
     return encodeURIComponent(editor.getValue())
}

function setEditable(editable) {
    editor.options.readOnly = !editable
}

