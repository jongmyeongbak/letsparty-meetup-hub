$(function() {
	const currentPath = window.location.pathname;
	const partyNo = currentPath.split("/")[2];
	/*	let clickedEvent;
		 부트스트랩의 모달객체 생성하기
		const eventModal = new bootstrap.Modal("#eventModal", {
			keyboard: false
		});
	
		 모달 엘리먼트에 이벤트 핸들러 함수 등록하기
		let el = document.querySelector("eventModal");*/

	var calendar = null;

	function clearEventField() {
		$(":input[name=title]").val("");
		$(":input[name=description]").val("");
		$(":input[name=startDate]").val("");
		$(":input[name=endDate]").val("");
		$(":input[name=startTime]").val("").prop("disabled", false);
		$(":input[name=endTime]").val("").prop("disabled", false);
		$(":checkbox[name=allDay]").prop("checked", false);
		$(":input[name=place]").val("");
		$(":input[name=noti]").val("");
	};

	//   document.addEventListener('DOMContentLoaded', function() {
	$(document).ready(function() {
		var Calendar = FullCalendar.Calendar;
		var Draggable = FullCalendar.Draggable;

		var containerEl = document.getElementById('external-events');
		var calendarEl = document.getElementById('calendar');
		var checkbox = document.getElementById('drop-remove');

		// 외부이벤트 초기화
		new Draggable(containerEl, {
			itemSelector: '.fc-event',
			eventData: function(eventEl) {
				return {
					title: eventEl.innerText
				};
			}
		});

		calendar = new Calendar(calendarEl, {
			customButtons: {
				myCustomButton: {
					text: '일정 만들기',
					click: function() {
						$("#eventModal").modal("show");
					}
				}
			},
			// 원하는 지정 날짜에 일정등록하기
			dateClick: function(info) {
				// 일정을 클릭하기
				let clickedDate = info.date;
				// 날짜가 삽입된 모달 열기
				openModalWithClickedDate(clickedDate);
				/*		        
								$("#eventModal").modal("show");
								var startDate = moment(info.event.start).format('YYYY-MM-DD');
									var endDate = moment(info.event.end).format('YYYY-MM-DD');
								$("#save-event").on("click", function(){
									var title = $("title").val();
									var description = $("description").val();
									var startDate = $("startDate").val();
									var endDate = $("endDate").val();
									var startTime = $("startTime").val();
									var endTime = $("endTime").val();
								})
				*/
			},
			// 일정을 수정하는 모달 열기
			eventClick: function(info) {
				clearEventField();
				openEventDetailModal(info.event);

			},
			events:

				function(info, successCallback, failureCallback) {
					console.log(info)

					$.ajax({
						url: '/event/events',
						type: 'GET',
						data: { partyNo: partyNo, startDate: moment(info.start).format("YYYY-MM-DD"), endDate: moment(info.end).format("YYYY-MM-DD") },
						success: function(data) {
							let events = [];

							data.forEach(function(eventData) {
								let event = {
									id: eventData.id,
									title: eventData.title,
									start: eventData.start,
									end: eventData.end,
									allDay: eventData.allDay,
									extendedProps: {
										description: eventData.description
									}
								};

								events.push(event);
							});

							successCallback(events);
							calendar.render();
						},
						error: function(error) {
							console.log(error)
							failureCallback();
						}
					});
				},

			headerToolbar: {
				left: 'prev,next,today',
				center: 'title',
				right: 'myCustomButton'
				//right: 'dayGridMonth,timeGridWeek,timeGridDay'
			},
			// 수정가능여부 
			editable: true,
			// 드래그 허용
			droppable: true,
			displayEventTime: false,
			drop: function(info) {
				// 드래그 후 제거 허용
				if (checkbox.checked) {
					// 드래그 가능한 이벤트를 목록에서 제거
					info.draggedEl.parentNode.removeChild(info.draggedEl);

				}
			},

			locale: 'ko',
		});
		/*
				// 일정 클릭 시 팝업으로 일정 정보 표시
				calendar.setOption('eventClick', function(info) {
				  var startDate = moment(info.event.start).format('YYYY-MM-DD');
				  var endDate = moment(info.event.end).format('YYYY-MM-DD');
					
				  alert('일정 제목: ' + info.event.title + '\n시작: ' + startDate + '\n종료: ' + endDate);
				});
				calendar.render();
		*/


		$(":checkbox[name=allDay]").change(function() {
			if ($(this).prop('checked')) {
				$(":input[name=startTime]").prop("readOnly", true);
				$(":input[name=endTime]").prop("readOnly", true);
			} else {
				let startTime = moment().format('HH:mm');
				let endTime = moment().add('1', 'h').format('HH:mm');
				$(":input[name=startTime]").prop("readOnly", false).val(startTime);
				$(":input[name=endTime]").prop("readOnly", false).val(endTime);
			}
		})

		$("#save-event").click(function() {
			let eventData = generateEventData();
			eventData['partyNo'] = partyNo;
			registerEvent(eventData);
			$("#eventModal").modal("hide");
		});
		/*
		$(document).on("click", ".edit-event-button", function() {
			// 클릭한 버튼의 data-eventno 속성 값을 가져옴
			var eventNo = $(this).data("eventno"); 
			console.log("Clicked edit button with eventNo:", eventNo);
		    
			$.ajax({
				url: '/event/${eventNo}',
				type: 'GET',
				dataType: 'json',
				success: function(response) {
					console/log('이벤트 검색', response);
					
					var title = response.title;
					var description = response.description;
				},
				error: function(xhr, status, error) {
					console.error('이벤트 검색 에러', error);
				}
			});
	
		});	
		*/
		// 여기서 eventNo를 활용하여 해당 이벤트에 대한 정보를 가져와서 모달에 설정하는 등의 작업을 수행
		$("#modify-event").click(function() {
			let eventId = $("#eventDetailModal input[name='id']").val();
			let eventData = generateEventDataForModification();

			/*			let allDay = $(":checkbox[name=allDay]:checked").val();
						if (allDay) {	// 체크되었다면
							eventData['allDay'] = 1;
						} else {
							eventData['allDay'] = 0;
							eventData['startTime'] = $(":input[name=startTime]").val();
							eventData['endTime'] = $(":input[name=endTime]").val();
						}
			*/


			// 이벤트 업데이트 요청
			/*			$.ajax({
							url: `/event/${eventNo}`,
							type: 'GET',
							data: eventData,
							dataType: 'json',
							success: function(response) {
			
								// 수정된 이벤트 정보로 캘린더에서 찾아 업데이트
								let updatedEvent = calendar.getEventById(eventNo);
								if (updatedEvent) {
									updatedEvent.setExtendedProp('title', title);
									updatedEvent.setExtendedProp('description', description);
									updatedEvent.setStart(startDate + "T" + startTime);
									updatedEvent.setEnd(endDate + "T" + endTime);
								}
			
								$("#eventDetailModal").modal("hide");
							},
							error: function(error) {
								console.error("Error updating event:", error);
							}
						});*/
			$.ajax({
				url: `/event/update/${eventId}`,
				type: 'POST',
				data: eventData,
				success: function() {
					let eventToUpdate = calendar.getEventById(eventId);
					eventToUpdate.setProp('title', eventData.title);
					let start = new Date(eventData.start);
					start.setHours(start.getHours() - 9);
					let end = new Date(eventData.end);
					end.setHours(end.getHours() - 9);
					eventToUpdate.setDates(start, end, { allDay: eventData.allDay });
					calendar.getEventById(eventId).setExtendedProp('description', eventData.description);
					$("#eventDetailModal").modal("hide");
				},
				error: function(error) {
					$("#eventDetailModal").modal("hide");
					console.error("Error updating event:", error);
				}
			});

		});

		function registerEvent(eventData) {
			$.ajax({
				url: '/event/register',
				type: 'POST',
				data: eventData,
				dataType: 'json'
			})
				.done(function(eventData) {
					calendar.addEvent(eventData);
				})
		}
	});

	// 날짜가 적힌 모달 함수 열기
	function openModalWithClickedDate(clickedDate) {
		$("#eventModal").modal("show");

		let now = moment(); // 현재 시각을 얻습니다.
		let formattedStartDate, formattedEndDate, startTime, endTime;

		if (moment(clickedDate).isSame(now, 'day')) { // 클릭한 날짜가 오늘인 경우
			formattedStartDate = now.add(1, 'hours').format('YYYY-MM-DD');
			startTime = now.startOf('hour').format('HH:mm');
			formattedEndDate = now.add(1, 'hours').format('YYYY-MM-DD');
			endTime = now.startOf('hour').format('HH:mm');
		} else { // 클릭한 날짜가 오늘이 아닌 경우
			formattedStartDate = moment(clickedDate).format('YYYY-MM-DD');
			formattedEndDate = moment(clickedDate).format('YYYY-MM-DD');
			startTime = "08:00";
			endTime = "09:00";
		}

		$("#startDate").val(formattedStartDate);
		$("#startTime").val(startTime);
		$("#endDate").val(formattedEndDate);
		$("#endTime").val(endTime);

	}

	function openEventDetailModal(event) {
		$("#eventDetailModal").modal("show");
		$("#eventDetailModal input[name='id']").val(event.id);
		$("#eventDetailModal input[name='title']").val(event.title);
		$("#eventDetailModal input[name='description']").val(event.extendedProps.description);

		// 체크박스의 상태를 설정합니다.
		$("#eventDetailModal input[name='allDay']").prop('checked', event.allDay);

		// 이벤트의 시작 및 종료 날짜 및 시간 설정
		let start = moment(event.start);
		let end = moment(event.end);
		if (!event.allDay) {
			$("#eventDetailModal input[name='startTime']").val(start.format('HH:mm'));
			$("#eventDetailModal input[name='endTime']").val(end.format('HH:mm'));
		} else {
			end.subtract(1, 'days');
		}
		$("#eventDetailModal input[name='startDate']").val(start.format('YYYY-MM-DD'));
		$("#eventDetailModal input[name='endDate']").val(end.format('YYYY-MM-DD'));
	}

	function generateEventData() {
		let eventData = {
			title: $(":input[name=title]").val(),
			description: $(":input[name=description]").val(),
		};
		let startDate = $(":input[name=startDate]").val();
		let endDate = $(":input[name=endDate]").val();
		let startTime = $(":input[name=startTime]").val();
		let endTime = $(":input[name=endTime]").val();
		let allDay = $(":checkbox[name=allDay]").prop('checked');
		eventData['allDay'] = allDay;
		if (allDay) {
			eventData['start'] = new Date(`${startDate}T00:00:00Z`).toISOString();
			endDate = new Date(`${endDate}T00:00:00Z`);
			endDate.setDate(endDate.getDate() + 1);
			eventData['end'] = endDate.toISOString();
		} else {
			eventData['start'] = new Date(`${startDate}T${startTime}:00Z`).toISOString();
			eventData['end'] = new Date(`${endDate}T${endTime}:00Z`).toISOString();
		}
		return eventData;
	}

	function generateEventDataForModification() {
		let eventData = {
			title: $("#eventDetailModal input[name='title']").val(),
			description: $("#eventDetailModal input[name='description']").val(),
		};
		let startDate = $("#eventDetailModal input[name='startDate']").val();
		let endDate = $("#eventDetailModal input[name='endDate']").val();
		let startTime = $("#eventDetailModal input[name='startTime']").val();
		let endTime = $("#eventDetailModal input[name='endTime']").val();
		let allDay = $("#eventDetailModal input[name='allDay']").prop('checked');
		eventData['allDay'] = allDay;
		if (allDay) {
			eventData['start'] = new Date(`${startDate}T00:00:00Z`).toISOString();
			endDate = new Date(`${endDate}T00:00:00Z`);
			endDate.setDate(endDate.getDate() + 1);
			eventData['end'] = endDate.toISOString();
		} else {
			eventData['start'] = new Date(`${startDate}T${startTime}:00Z`).toISOString();
			eventData['end'] = new Date(`${endDate}T${endTime}:00Z`).toISOString();
		}
		return eventData;
	}
	/*	var calendar = new FullCalendar.Calendar(calendarEl, {
			initialView: 'dayGridMonth',
			locale: "ko"
		});
		calendar.render();*/
});