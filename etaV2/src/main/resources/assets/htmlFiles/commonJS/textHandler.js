

//alert("You are running the " + navigator.appName + " browser.");




document.querySelector('body').addEventListener("keydown", (ev => {
	console.log(ev);
	if(ev.code === "AltRight")
	{
		document.getElementById('testTextZone').innerText += "altgr"
	}
	else
	{
		document.getElementById('testTextZone').innerText += ev.shiftKey;
	}
	document.getElementById('testTextZone').innerText += ('|'+ String.fromCharCode(ev.keyCode)+'/');
}));