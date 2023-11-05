let input = document.getElementById('input');
let span = document.getElementById('span');

input.addEventListener('change', () => {
   let files = input.files;
var size = parseFloat(input.files[0].size / (1024 * 1024)).toFixed(2);
   if(files.length > 0) {
    if(size > 2){
        span.innerText = 'File Size Should not exceed 2MB';
        alert("Please Select Size Less Than 2 MB");
         document.getElementById('input').value="";
        return;
    }
   }

   span.innerText = '';
});



//  var image=document.getElementById('input').value;
//
//        var image=document.getElementById('input');
//        var size = parseFloat(image.files[0].size / (1024 * 1024)).toFixed(2);
//        if (size > 2){
//            alert("Please Select Size Less Than 2 MB");
//           document.getElementById('input').value="";
//           //document.getElementById('file_name').innerHTML="Choose file";exit;
//        } else {
//             alert("Valid File");
//        }
//      }

