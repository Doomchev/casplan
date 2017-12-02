// Runtime library for Casplan code cross-compiled to JavaScript
function createObject_(parent, applyingObject) {
	let object = function() {
		let func = arguments.callee;
		if(!func.create_) return {};
		let object = {};
		object.__proto__ = func;
		func.create_.apply(object, arguments);
		return object;
	}
	if(parent) object.__proto__ = parent;
	if(applyingObject) for(let attr in applyingObject) object[attr] = applyingObject[attr];
	return object;
}

function createList_(quantity, initialValue) {
	if(quantity === undefined) quantity = 0;
	let list = new Array(quantity);
	if(initialValue !== undefined) {
		for(let n = 0; n < quantity; n++) list[n] = initialValue;
	}
	return list;
}

canvas = document.createElement("canvas");
ctx = canvas.getContext("2d");

external = {
	gui: {
		window: {}
	},
	texture: {
		load: function(fileName) {
			let img = imageMap_[fileName];
			img.draw = function(x, y, sourceX, sourceY, width, height) {
				if(x === undefined) x = 0;
				if(y === undefined) y = 0;
				if(sourceX === undefined) sourceX = 0;
				if(sourceY === undefined) sourceY = 0;
				if(width === undefined) width = img.width;
				if(height === undefined) height = img.height;
				ctx.drawImage(this, sourceX, sourceY, width, height, x, y, width, height)
			}
			return img;
		}
	}
}

imageMap_ = {};
window.onload = function() {
	document.body.appendChild(canvas);
	canvas.width = window.innerWidth;
	canvas.height = window.innerHeight;
	external.gui.window.width = canvas.width;
	external.gui.window.height = canvas.height;
	let imagesToLoad = images_.length
	for(let n = 0; n < images_.length; n++) {
		let fileName = images_[n];
		let img = new Image();
		img.src = fileName;
		img.onload = function() {
			imagesToLoad--;
			if(imagesToLoad == 0) main_();
		}
		imageMap_[fileName] = img;
	}
}

function showMessage(message) {
	render(true);
	setTimeout(function() {
		alert(message)
	}, 1);
}

function print(message) {
	console.log(message);
}

wnd_ = external.gui.window;
window.onclick = function(e) {
	if(wnd_.onClick) wnd_.onClick(e.clientX, e.clientY)
}

function render(once) {
	ctx.clearRect(0, 0, wnd_.width, wnd_.height);
	if(wnd_.render) wnd_.render();
	if(!once) setTimeout(render, 15);
}
render();