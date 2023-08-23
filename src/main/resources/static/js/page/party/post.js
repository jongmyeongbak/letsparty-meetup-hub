$(function(){
	// 지도 아이콘을 누르면 모달을 띄운다.
	$("#open-map").on("click", function() {
	    $("#modal").modal("show");
	})
	
	// 모달이 완전히 띄워졌을 때 지도 객체를 다시 초기화하여 전체적인 지도 이미지를 렌더링
	$("#modal").on('shown.bs.modal', function() {
		map.relayout();
		// 초기에 설정해둔 키워드로 검색 함수를 실행시켜 중심좌표를 중앙으로 맞춘다.
		searchPlaces();
	})
})		

// 카카오 API
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

// 키워드로 장소를 검색합니다
searchPlaces();

// 키워드 검색을 요청하는 함수입니다
function searchPlaces(address_name, place_name) {
	// 지호 수정 사항
	// 	- address_name + place_name
	//	  목록을 클릭했을 해당 장소로 이동할 수 있도록, 두개의 주소 정보를 조합해서 검색시킨다.
	//	- document.getElementById('keyword').value;
	// 	  최초 실행시 초기 설정값인 "중앙HTA"의 검색결과를 보여주기 위해 초기 키워드 값을 대입.
    var keyword = address_name + place_name || document.getElementById('keyword').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
    ps.keywordSearch(keyword, placesSearchCB); 

 	// 지호 추가 사항
 	// - 실제로는 두 주소의 정보를 조합하여 정확한 위치를 찾아냈지만, 사용자가 자신이 찾고자 했던 위치를 쉽게
 	//	 인식할 수 있도록 정소검색 후 키워드 창에는 장소이름이 대입된다.
    document.getElementById('keyword').value = place_name || document.getElementById('keyword').value;
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
    console.log(places)
    
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {
	console.log(places.id)
    var el = document.createElement('li'),
    // 지호 추가 사항 목록을 클릭했을 때 해당 searchPlaces메소드에 주소명과 장소명을 둘 다 넣어,
    // 정확도를 높힌 뒤 목록이 가르키는 장소로 중심 좌표를 이동시킨다.
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
	
	// 생성된 리스트 엘리먼트를 클릭했을 때 만들어둔 객체에 값을 담는 함수 실행
	el.onclick = function() {
        
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

// 장소 데이터를 담는 객체
let selectedPlace = {};

// selectedPlace객체에 데이터를 담는 함수
function selectPlace(place) {
    selectedPlace = {
        id: place.id,
        name: place.place_name,
        address: place.address_name,
        road_address: place.road_address_name,
        phone: place.phone
    };
    console.log(selectedPlace);
}

// 첨부 버튼 클릭 이벤트
$("#attachButton").on("click", function() {
    // 선택된 장소 데이터를 HTML에 적용
    if (selectedPlace.name && selectedPlace.address) {
        $('#placeName').html('<strong>' + selectedPlace.name + '</strong>');
        $('#loadLocation').text(selectedPlace.address);
        $("#modal").modal("hide");
    } else {
        alert('장소를 선택해주세요!');
    }
});



