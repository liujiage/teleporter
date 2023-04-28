function detectBody() {
  if(document.body){
    protocol = window.location.protocol
    hostname = window.location.hostname

    var libs = [
    protocol+'//'+hostname+'/TELEPORTER_JS/jquery-3.3.1.js',
    protocol+'//'+hostname+'/TELEPORTER_JS/jquery.qrcode.min.js',
    protocol+'//'+hostname+'/TELEPORTER_JS/teleporter.js'
    ];
    var injectLibFromStack = function(){
      if(libs.length > 0){
        
        //grab the next item on the stack
        var nextLib = libs.shift();
        var headTag = document.getElementsByTagName('body')[0];
        
        //create a script tag with this library
        var scriptTag = document.createElement('script');
        scriptTag.src = nextLib;
        
        //when successful, inject the next script
        scriptTag.onload = function(e){
          console.log("---> loaded: " + e.target.src);
          injectLibFromStack();
        };    
        
        //append the script tag to the
        headTag.appendChild(scriptTag);
        console.log("injecting: " + nextLib);
      }
      else return;
    }
  
  //start script injection
  injectLibFromStack();
  }
  else 
    setTimeout(detectBody, 1);  // keep trying until body is available
}
detectBody();     