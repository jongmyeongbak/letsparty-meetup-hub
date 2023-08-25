$(function(){
	// 모달이 완전히 띄워졌을 때 지도 객체를 다시 초기화하여 전체적인 지도 이미지를 렌더링
	$("#place-modal").on('shown.bs.modal', function() {
		map.relayout();
		// 초기에 설정해둔 키워드로 검색 함수를 실행시켜 중심좌표를 중앙으로 맞춘다.
		searchPlaces();
		console.log("임시저장소:" + tempSelectedPlace.name);
  		console.log("실제저장소:" + selectedPlace.name);
	})
})		

/* 카카오 API 시작 */
// 마커를 담을 배열입니다
var markers = [];

var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
	mapOption = {
    center: new kakao.maps.LatLng(0, 0), // 지도의 중심좌표
    level: 3 // 지도의 확대 레벨
};  

// 지도를 생성합니다    
var map = new kakao.maps.Map(mapContainer, mapOption); 

// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places();  

// 검색 결과 목록이나 마커를 클릭했을 때 장소명을 표출할 인포윈도우를 생성합니다
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

// 지호 추가
// 임시로 선택된 장소 데이터를 담을 객체
let tempSelectedPlace = {};
// 실제 전송될  장소 데이터를 담는 객체
let selectedPlace = {};

// 키워드로 장소를 검색합니다
searchPlaces();

// 키워드 검색을 요청하는 함수입니다
function searchPlaces(address_name, place_name) {
	// 지호 수정 사항
	// 	- address_name + place_name
	//	  목록을 클릭했을 해당 장소로 이동할 수 있도록, 두개의 주소 정보를 조합해서 keyword변수에 대입
	//	- $("#keyword").val();
	// 	  최초 실행시 초기 검색창 설정값인 "중앙HTA"의 검색결과를 보여주거나,
	//	  리스트를 클릭해서 검색하는 게 아닌 키워드를 입력해 검색시에 검색창 키워드 값을 keyword변수에 대입.
    let keyword = address_name + place_name || $("#keyword").val()

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }
 	// 지호 추가 사항
 	// - 실제로는 두 주소의 정보를 조합하여 정확한 위치를 찾아냈지만, 사용자가 자신이 찾고자 했던 위치를 쉽게
 	//	 인식할 수 있도록 장소검색 후 키워드 창에는 장소이름만 대입한다.
    $("#keyword").val(place_name || $("#keyword").val());

    // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
    ps.keywordSearch(keyword, placesSearchCB); 

}

// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {

        // 정상적으로 검색이 완료됐으면
        // 검색 목록과 마커를 표출합니다
        displayPlaces(data);

        // 페이지 번호를 표출합니다
        displayPagination(pagination);

    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {

        alert('검색 결과가 존재하지 않습니다.');
        return;

    } else if (status === kakao.maps.services.Status.ERROR) {

        alert('검색 결과 중 오류가 발생했습니다.');
        return;
    }
}

// 검색 결과 목록과 마커를 표출하는 함수입니다
function displayPlaces(places) {

    var listEl = document.getElementById('placesList'), 
    menuEl = document.getElementById('menu_wrap'),
    fragment = document.createDocumentFragment(), 
    bounds = new kakao.maps.LatLngBounds(), 
    listStr = '';

    // 검색 결과 목록에 추가된 항목들을 제거합니다
    removeAllChildNods(listEl);

    // 지도에 표시되고 있는 마커를 제거합니다
    removeMarker();

    for ( var i=0; i<places.length; i++ ) {

        // 마커를 생성하고 지도에 표시합니다
        var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x),
            marker = addMarker(placePosition, i), 
            itemEl = getListItem(i, places[i]); // 검색 결과 항목 Element를 생성합니다

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
        // LatLngBounds 객체에 좌표를 추가합니다
        bounds.extend(placePosition);

        // 마커와 검색결과 항목에 mouseover 했을때
        // 해당 장소에 인포윈도우에 장소명을 표시합니다
        // mouseout 했을 때는 인포윈도우를 닫습니다
        (function(marker, title) {
            kakao.maps.event.addListener(marker, 'mouseover', function() {
                displayInfowindow(marker, title);
            });

            kakao.maps.event.addListener(marker, 'mouseout', function() {
                infowindow.close();
            });

            itemEl.onmouseover =  function () {
                displayInfowindow(marker, title);
            };

            itemEl.onmouseout =  function () {
                infowindow.close();
            };
        })(marker, places[i].place_name);

        fragment.appendChild(itemEl);
    }

    // 검색결과 항목들을 검색결과 목록 Element에 추가합니다
    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;

    // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
    map.setBounds(bounds);
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {
    var el = document.createElement('li'),
    // 지호 수정 사항 - 목록을 클릭했을 때 searchPlaces메소드를 실행시키는 onclick 이벤트를 등록한다.
    // searchPlaces메소드를 주소명과 장소명을 매개변수로 받을 수 있도록 수정하여 정확도를 높힌다.
    itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
                '<div class="info" onclick="searchPlaces(\'' + places.address_name + '\', \'' + places.place_name + '\' );">' +
                '   <h5>' + places.place_name + '</h5>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
                    '   <span class="jibun gray">' +  places.address_name  + '</span>';
    } else {
        itemStr += '    <span>' +  places.address_name  + '</span>'; 
    }

      itemStr += '  <span class="tel">' + places.phone  + '</span>' +
                '</div>';           

    el.innerHTML = itemStr;
    el.className = 'item';
	
	// 지호 수정 사항 - 생성된 리스트 엘리먼트를 클릭했을 때 만들어둔 임시 저장 객체에 값을 담는 함수 실행
	el.onclick = function() {
        console.log(places);
        selectPlace(places);
    };
    
    return el;
}

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position, idx, title) {
    var imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
        imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
        imgOptions =  {
            spriteSize : new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin : new kakao.maps.Point(0, (idx*46)+10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
            marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage 
        });

    marker.setMap(map); // 지도 위에 마커를 표출합니다
    markers.push(marker);  // 배열에 생성된 마커를 추가합니다

    return marker;
}

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    for ( var i = 0; i < markers.length; i++ ) {
        markers[i].setMap(null);
    }   
    markers = [];
}

// 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
    var paginationEl = document.getElementById('pagination'),
        fragment = document.createDocumentFragment(),
        i; 

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild (paginationEl.lastChild);
    }

    for (i=1; i<=pagination.last; i++) {
        var el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i===pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function(i) {
                return function() {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

// 검색결과 목록 또는 마커를 클릭했을 때 호출되는 함수입니다
// 인포윈도우에 장소명을 표시합니다
function displayInfowindow(marker, title) {
    var content = '<div style="padding:5px;z-index:1;">' + '<div style="font-size: larger; margin-bottom: 5px;">이 위치를 추가</div>' + title + '</div>';

    infowindow.setContent(content);
    infowindow.open(map, marker);
}

 // 검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {   
    while (el.hasChildNodes()) {
		el.removeChild (el.lastChild);
    }
}   
/* 카카오 API 끝 */

// tempSelectedPlace객체에 데이터를 담는 함수
function selectPlace(place) {
    tempSelectedPlace = {
        placeId: place.id,
        placeName: place.place_name,
        addressName: place.address_name,
        roadAddressName: place.road_address_name
    };
}

// 리스트를 선택하여 데이터를 임시 저장소에 저장하고,
// 첨부 버튼을 눌러야 실제 전송될 객체에 장소 데이터를 담는다.
$("#attachButton").on("click", function() {
    // 선택된 장소 데이터를 HTML에 적용
    if (tempSelectedPlace.placeName && tempSelectedPlace.addressName) {
		selectedPlace = tempSelectedPlace; // 첨부버튼을 누를 때 실제 저장소에 옮긴다.
        $('#placeName').html('<strong>' + selectedPlace.placeName + '</strong>');
        $('#loadLocation').text(selectedPlace.addressName);
        $("#place-modal").modal("hide");
        $("#place-short-form").removeClass("d-none")
    } else {
        alert('장소를 선택해주세요!');
    }
    
// 첨부 삭제 클릭 이벤트
$("#place-delete-btn").on("click", function(e){
	e.preventDefault();
	console.log(selectedPlace)
	
	//객체 초기화
	selectedPlace = {};
	tempSelectedPlace = {};
	console.log(selectedPlace)
	$("#place-short-form").addClass("d-none")
})

// 장소 데이터 전송
$("#add-post-btn").on("click", function(e){
	e.preventDefault();
	// 객체 안에 데이터가 있는지 체크하고 있으면 hidden input에 값 대입 후 데이터 전송 
	if(Object.keys(selectedPlace).length){
		$("#place-id").val(selectedPlace.placeId);
		$("#place-name").val(selectedPlace.placeName);
		$("#place-address-name").val(selectedPlace.addressName);
		$("#place-road-address-name").val(selectedPlace.roadAddressName);
	}
	
	$("#party-post-form").submit();
})

});



