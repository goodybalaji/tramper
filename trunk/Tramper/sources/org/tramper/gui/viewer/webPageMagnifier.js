var selectedId;
var idIndex = 1;
var linkNumber = 1;
var highlighter;

function selectItem(id) {
	var f2sItem = document.getElementById(id);
	if (f2sItem) {
		// find the position of the target object in the page 
	    var posx = 0; 
	    var posy = 0; 
	    var target = f2sItem; 
	    if (f2sItem.offsetParent) { 
	            do { 
	                    posx += f2sItem.offsetLeft; 
	                    posy += f2sItem.offsetTop; 
	            } while (f2sItem = f2sItem.offsetParent); 
	    } 
		highlighter.style.left = posx+"px";
		highlighter.style.top = posy+"px";
		highlighter.style.width = target.offsetWidth+"px";
		highlighter.style.height = target.offsetHeight+"px";

		selectedId = id;
		/*if (f2sItem.focus) {
			f2sItem.focus();
		}*/
		
		//scroll the window to show the item if necessary 
	    //window.scroll(0, posy);
	}
}

function onClickItem(event) {
	if (event) {
		event.stopPropagation();
	} else {
		window.event.cancelBubble = true;
		event = window.event;
	}
	var trgt;
	if (event.target) {
		trgt = event.target;
	} else if (event.srcElement) {
		trgt = event.srcElement;
	}
	
	if (trgt) {
		// send the command to the browser
		window.status = "command:"+trgt.id;
		selectItem(trgt.id);
	 }
}

function depthFirstSearch(node) {
    
    var nodeType = node.nodeType;
    if (nodeType == 1) {//element
        var nodeName = node.nodeName.toLowerCase();
        if (nodeName == "p" || 
            nodeName == "div" || 
            nodeName == "td" || 
            nodeName == "th" || 
            nodeName == "li" || 
            nodeName == "body" || 
            nodeName == "form") {

            var nodeId = node.getAttribute("id");
            if (nodeId === "") {
	            nodeId = "tramper"+(idIndex++);
	            node.setAttribute("id", nodeId);
	        }
	        node.onclick = onClickItem;
	        
        } else if (nodeName == "a") {

            var linkId = node.getAttribute("id");
            if (linkId === "") {
	            linkId = "tramper"+(idIndex++);
	            node.setAttribute("id", linkId);
	        }
        	
            var linkNumNode = document.createElement("sup");
            linkNumNode.style.color = "red";
            linkNumNode.style.position = "relative";
            linkNumNode.style.backgroundColor = "transparent";
			linkNumNode.style.opacity = "0.5";
			linkNumNode.style.filter = "alpha(opacity=50)";
            linkNumNode.style.textDecoration = "none";
            linkNumNode.innerHTML = "(" + (linkNumber++) + ")";
            node.appendChild(linkNumNode);
        }
    }
    
	var childNodes = node.childNodes;
	for (var i=0; i<childNodes.length; i++) {
	    var aChild = childNodes[i];
	    depthFirstSearch(aChild);
	}
}

function initPage() {
	var bodyElem = document.getElementsByTagName("body")[0];
	depthFirstSearch(bodyElem);
	
    highlighter = document.createElement("span");
	highlighter.style.position = "absolute";
	highlighter.style.left = "0px";
	highlighter.style.top = "0px";
	highlighter.style.width = "0px";
	highlighter.style.height = "0px";
	highlighter.style.backgroundColor = "transparent";
	highlighter.style.zIndex = "999999";
	highlighter.style.border = "2px dashed red";
	bodyElem.appendChild(highlighter);
}

function frameItem(id) {
	var f2sItem = document.getElementById(id);
	if (f2sItem) {
		f2sItem.style.border = '1px dashed Highlight';
	}
}

function zoomPage(scale) {
	var bodyElem = document.getElementsByTagName("body");
	bodyElem[0].style.fontSize = bodyElem[0].style.fontSize + scale;
}
