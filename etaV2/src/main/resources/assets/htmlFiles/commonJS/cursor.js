class Pos{
	constructor (){
		this.line = 0;
		this.column = 0;
	}
}

class Cursor
{
	constructor (element)
	{
		this.div = element;

		this.fakeEl =  window.document.createElement('span');
		this.fakeEl.style.visibility = "hidden";
		window.document.body.appendChild(this.fakeEl);

		let computedStyle = window.getComputedStyle(this.div, null);
		this.font = computedStyle.getPropertyValue("font-weight") + " ";
		this.font += computedStyle.getPropertyValue("font-size") + "/";
		//alert(this.lineHeight);
		this.font += computedStyle.lineHeight + " ";
		this.font += computedStyle.getPropertyValue("font-family");
		this.lineHeight = parseInt(computedStyle.lineHeight);
		this.pos = new Pos();




		var that = this
		setInterval(function () {
			that.hideAndAppear();
		}, 500);
	}

	hideAndAppear() {
		this.blink = !this.blink;
		if (this.blink) {
			this.div.style.visibility = 'hidden';
		} else {
			this.div.style.visibility = 'inherit';
		}
	}

	calculateLengthText(text, font) {

		this.fakeEl.innerHTML = text;
		this.fakeEl.style.font= font;
		let width = this.fakeEl.offsetWidth;
		this.fakeEl.innerHTML = "";
		return width;
	}


	setY(numbreOfCarater) {
		this.pos.column = numbreOfCarater;
		let text = "";
		for(let i =0; i < numbreOfCarater; i++)
		{
			text += "a";
		}
		let temp = this.calculateLengthText(text,this.font);
		this.div.style.left = temp +"px";
	}

	setX (lineNumber) {
		this.pos.column = numbreOfCarater;
		let temp = (lineNumber*this.lineHeight);
		this.div.style.top = temp+"px";
	}

	recaulateSettings () {
		let computedStyle = window.getComputedStyle(this.div, null);
		this.font = computedStyle.getPropertyValue("font-weight") + " ";
		this.font += computedStyle.getPropertyValue("font-size") + "/";
		this.lineHeight =computedStyle.getPropertyValue("line-height");
		this.font += this.lineHeight + " ";
		this.font += computedStyle.getPropertyValue("font-family");
	}
}