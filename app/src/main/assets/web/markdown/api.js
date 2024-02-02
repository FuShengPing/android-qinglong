function init(){
    const options = {
        async: true,
        pedantic: false,
        gfm: true,
    }
    marked.use(options)
}

function setContent(text) {
    text = decodeURIComponent(text)
    marked.parse(text).then((result) => {
        document.getElementById("markdown").innerHTML = result
    })
}

