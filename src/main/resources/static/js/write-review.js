// Quill Editor 초기화
const quill = new Quill('#editor', {
    theme: 'snow',
    modules: {
        toolbar: [
            [{ 'header': [1, 2, 3, false] }],
            ['bold', 'italic', 'underline'],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
            ['link'],
            ['clean']
        ]
    }
});

let selectedRating = 0;
let selectedBook = null;

document.addEventListener('DOMContentLoaded', function() {
    // 명언 글자수 카운터
    document.getElementById('quote').addEventListener('input', function() {
        document.getElementById('quoteLength').textContent = this.value.length;
    });

    // 엔터키로 검색
    document.getElementById('bookSearch').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchBooks();
        }
    });
});

// 별점 설정
function setRating(rating) {
    selectedRating = rating;
    const stars = document.querySelectorAll('.star');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });
}

// 모달 열기
function openBookModal() {
    document.getElementById('bookModal').classList.remove('hidden');
}

// 모달 닫기
function closeBookModal() {
    document.getElementById('bookModal').classList.add('hidden');
}

// 책 검색
async function searchBooks() {
    const query = document.getElementById('bookSearch').value;
    if (!query.trim()) {
        alert('검색어를 입력하세요');
        return;
    }

    const bookList = document.getElementById('bookList');
    const loading = document.getElementById('bookLoading');

    // 로딩 표시
    bookList.innerHTML = '';
    loading.classList.remove('hidden');

    try {
        const response = await fetch(`/api/external/books/search?query=${encodeURIComponent(query)}`);
        const data = await response.json();

        // 로딩 숨김
        loading.classList.add('hidden');

        if (data.documents && data.documents.length > 0) {
            data.documents.forEach(book => {
                const bookItem = document.createElement('div');
                bookItem.className = 'flex gap-4 p-4 border-2 border-gray-200 rounded-lg cursor-pointer transition-all hover:border-green-500 hover:bg-gray-50';
                bookItem.onclick = () => selectBook(book);
                bookItem.innerHTML = `
                    <img src="${book.thumbnail}" alt="${book.title}" class="w-15 h-21 object-cover rounded">
                    <div class="flex-1">
                        <div class="font-semibold mb-1">${book.title}</div>
                        <div class="text-gray-600 text-sm">${book.authors.join(', ')}</div>
                    </div>
                `;
                bookList.appendChild(bookItem);
            });
        } else {
            bookList.innerHTML = '<p class="text-gray-500">검색 결과가 없습니다.</p>';
        }
    } catch (error) {
        console.error('검색 오류:', error);
        loading.classList.add('hidden');
        bookList.innerHTML = '<p class="text-red-500">검색에 실패했습니다. 다시 시도해주세요.</p>';
    }
}

// 책 선택
function selectBook(book) {
    selectedBook = {
        title: book.title,
        author: book.authors.join(', '),
        isbn: book.isbn,
        thumbnail: book.thumbnail
    };

    document.getElementById('selectedBookTitle').textContent = selectedBook.title;
    document.getElementById('selectedBookAuthor').textContent = selectedBook.author;
    document.getElementById('selectedBookThumbnail').src = selectedBook.thumbnail;
    document.getElementById('selectedBook').classList.remove('hidden');
    document.getElementById('selectedBook').classList.add('flex');

    closeBookModal();
}

// 리뷰 제출
async function submitReview() {
    const title = document.getElementById('reviewTitle').value;
    const quote = document.getElementById('quote').value;
    const content = quill.root.innerHTML;

    if (!title.trim()) {
        alert('리뷰 제목을 입력하세요');
        return;
    }

    if (!selectedBook) {
        alert('책을 선택하세요');
        return;
    }

    if (selectedRating === 0) {
        alert('별점을 선택하세요');
        return;
    }

    if (!quote.trim()) {
        alert('명언을 입력하세요');
        return;
    }

    if (quote.trim().length < 5 || quote.trim().length > 100) {
        alert('명언은 5~100자로 입력하세요');
        return;
    }

    if (!content.trim() || content === '<p><br></p>') {
        alert('리뷰 내용을 입력하세요');
        return;
    }

    try {
        const response = await fetch('/api/reviews', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                title: title,
                bookTitle: selectedBook.title,
                bookAuthor: selectedBook.author,
                bookIsbn: selectedBook.isbn,
                bookThumbnail: selectedBook.thumbnail,
                rating: selectedRating,
                quote: quote,
                content: content
            })
        });

        if (response.ok) {
            alert('리뷰가 등록되었습니다!');
            window.location.href = '/';
        } else {
            alert('리뷰 등록에 실패했습니다.');
        }
    } catch (error) {
        console.error('제출 오류:', error);
        alert('리뷰 등록에 실패했습니다.');
    }
}
