/*
	Telephasic by HTML5 UP
	html5up.net | @ajlkn
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
*/

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

    // Panel.
    $(
        '<div id="navPanel">' +
            '<nav>' +
                $('#nav').navList() +
            '</nav>' +
        '</div>'
    )
        .appendTo($body)
        .panel({
            delay: 500,
            hideOnClick: true,
            resetScroll: true,
            resetForms: true,
            side: 'top',
            target: $body,
            visibleClass: 'navPanel-visible'
    });

    $('#navButton').on('click', function() {
            const $navPanel = $("#navPanel");
            const $root = $(":root");

            console.log(`${$navPanel.outerHeight()}px`);

            $root.css("--navPanel-height", `${$navPanel.outerHeight()}px`);
        });



})(jQuery);