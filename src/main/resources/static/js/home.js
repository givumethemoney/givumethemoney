/*
	Telephasic by HTML5 UP
	html5up.net | @ajlkn
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
*/
let currentIndex = 0; // 현재 슬라이드 인덱스
const slides = document.querySelector('.slides'); // 슬라이드 요소

function showNextSlide() {
    currentIndex = (currentIndex + 1) % 3; // 3개의 이미지가 있으므로 인덱스를 순환
    const offset = -currentIndex * 100; // 이동할 오프셋 계산
    slides.style.transform = `translateX(${offset}%)`; // 슬라이드 이동
}

// 3초마다 다음 슬라이드를 보여주기
setInterval(showNextSlide, 3000);

(function($) {

	var	$window = $(window),
		$body = $('body');

	// Breakpoints.
		breakpoints({
			normal:    [ '1081px',  '1280px'  ],
			narrow:    [ '821px',   '1080px'  ],
			narrower:  [ '737px',   '820px'   ],
			mobile:    [ '481px',   '736px'   ],
			mobilep:   [ null,      '480px'   ]
		});

	// Play initial animations on page load.
		$window.on('load', function() {
			window.setTimeout(function() {
				$body.removeClass('is-preload');
			}, 100);
		});

	// Nav.

		// Button.
			$(
				'<div id="navButton">' +
					'<a href="#navPanel" class="toggle"></a>' +
				'</div>'
			)
				.appendTo($body);
})(jQuery);