console.log("Hello. Things are going well so far.");
console.log(config);

var ws = new WebSocket(config.endpoint);

ws.onopen = function (event) {
  console.log("WebSocket connected. That's good news.");
};

function display(obj) {
  var el = document.getElementById(obj.topic);
  if (!el) {
    el = document.createElement("div");
    el.id = obj.topic;
    var messagesDiv = document.getElementsByClassName("messages")[0];
    messagesDiv.appendChild(el);
  }
  el.innerHTML = obj.message;
}

ws.onmessage = function (event) {
  console.log("Got message:", event.data);
  display(JSON.parse(event.data));
};
