



function animationEnd(event) {
  if (event.animationName === 'disapear') {
    /*event.target.parentNode.removeChild(event.target);*/
    //naa we just wanna hide it for now
  }
}

document.body.addEventListener('animationend', animationEnd);
document.body.addEventListener('webkitAnimationEnd', animationEnd);