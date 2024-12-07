document.addEventListener("DOMContentLoaded", function () {
    // 초기 상태 설정
    const ongoingDonations = document.getElementById("ongoing-donations");
    const completedDonations = document.getElementById("completed-donations");
    const ongoingButton = document.getElementById("ongoingButton");
    const completedButton = document.getElementById("completedButton");
    const cards = document.querySelectorAll(".donation-card");

    // 기본 보이기 설정
    ongoingDonations.style.display = "block";
    completedDonations.style.display = "none";
    ongoingButton.classList.add("active");

    // 진행 중/전해진 버튼 클릭 이벤트
    ongoingButton.addEventListener("click", function () {
        ongoingDonations.style.display = "block";
        completedDonations.style.display = "none";
        this.classList.add("active");
        completedButton.classList.remove("active");
    });

    completedButton.addEventListener("click", function () {
        ongoingDonations.style.display = "none";
        completedDonations.style.display = "block";
        this.classList.add("active");
        ongoingButton.classList.remove("active");
    });

    // 카드 레이아웃 강제 설정
    cards.forEach((card) => {
        card.style.display = "flex";
        card.style.alignItems = "center";
        card.style.justifyContent = "space-between";
    });
});
