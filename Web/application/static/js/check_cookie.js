function validate_form(thisform)
{
with (thisform)
  {
    if(!document.getElementById("checkboxID").checked){
        alert("If you do not use cookie, you cannot login!");
        return false;
    }
  }
}