function openPopup(url) {
  let width = 400;
  let height = 644;
  let left = (window.innerWidth - width) / 2 + window.screenX;
  let top = (window.innerHeight - height) / 2 + window.screenY;

  console.log(left);
  console.log(top);
  let popup = window.open(
    url,
    "_blank",
    "width=" + width + ", height=" + height + ", left=" + left + ", top=" + top
  );

  if (popup) {
    popup.focus();
  } else {
    alert("팝업 차단을 해제해 주세요.");
  }
}
