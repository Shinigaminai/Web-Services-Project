
var loadHTML = function(subpage, element) {       // load HTML only page into element , script tags don't work!
    var xhr= new XMLHttpRequest();
    xhr.open('GET', subpage, true);
    xhr.onreadystatechange = function() {
        if (this.readyState!==4) return;
        if (this.status!==200) return; // or whatever error handling you want
        element.innerHTML = this.responseText;
    };
    xhr.send();
}