function get(path) {
    return new Promise(((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', path);
        xhr.onload = () => resolve(xhr.responseText);
        xhr.onerror = () => reject(xhr.statusText);
        xhr.send();
    }));
}

function post(path, body) {
    return new Promise(((resolve, reject) => {
        const xhr = new XMLHttpRequest();

        xhr.open('POST', path);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onload = () => resolve(xhr.responseText);
        xhr.onerror = () => reject(xhr.statusText);
        xhr.send(JSON.stringify(body));
    }))
}

function createTextTd(text) {
    let td = document.createElement("td");
    td.innerText = text;
    return td;
}

function createCheckboxTd(checked, spanText) {
    let td = document.createElement("td");
    let label = document.createElement("label");
    let input = document.createElement("input");
    input.type = "checkbox";
    input.checked = checked;
    input.setAttribute("class", "filled-in");
    let span = document.createElement("span")
    span.innerText = spanText;

    label.appendChild(input);
    label.appendChild(span);
    td.appendChild(label);

    return td;
}

function createInput(value, disabled= false, className = "validate") {
    let input = document.createElement("input");
    input.type = "text";
    input.value = value;
    if (disabled) {
        input.setAttribute("disabled", "");
    }

    return input;
}

function createLabel(labelText) {
    let label = document.createElement("label");
    label.innerText = labelText;
    return label;
}

function removeAllChildNodes(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
}
