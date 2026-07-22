// 책장(읽은 책) 캘린더 + 추가/수정/삭제 로직

let bsReading = [];      // 읽는 중 (finishedAt == null)
let bsFinished = [];     // 완독 (finishedAt != null)
let bsFinishedByDate = {}; // 'YYYY-MM-DD' -> [entry, ...]
let bsIsOwner = false;

let calYear = 0;         // 현재 표시 중인 달력 연/월(0-based)
let calMonth = 0;

let bsSelectedBook = null;   // 추가 모달에서 선택한 책
let bsEditingEntry = null;   // 상세/편집 모달에서 다루는 항목

document.addEventListener('DOMContentLoaded', () => {
    const panel = document.getElementById('panelShelf');
    if (!panel) return;
    bsIsOwner = panel.dataset.owner === 'true';

    bsReading = parseDataIsland('bookshelfReadingData');
    bsFinished = parseDataIsland('bookshelfFinishedData');

    bsFinishedByDate = {};
    bsFinished.forEach(e => {
        if (!e.finishedAt) return;
        (bsFinishedByDate[e.finishedAt] = bsFinishedByDate[e.finishedAt] || []).push(e);
    });

    // 달력 시작 위치: 가장 최근 완독 달, 없으면 이번 달
    const anchor = bsFinished.length > 0 && bsFinished[0].finishedAt
        ? new Date(bsFinished[0].finishedAt)
        : new Date();
    calYear = anchor.getFullYear();
    calMonth = anchor.getMonth();

    Modal.setupOutsideClick('addBookModal');
    Modal.setupOutsideClick('entryDetailModal');

    renderShelf();
});

function parseDataIsland(id) {
    const el = document.getElementById(id);
    if (!el) return [];
    try {
        return JSON.parse(el.textContent) || [];
    } catch {
        return [];
    }
}

// ── 탭 전환 ────────────────────────────────────────────────
function switchBookshelfTab(tab) {
    const isReviews = tab === 'reviews';
    document.getElementById('panelReviews').classList.toggle('hidden', !isReviews);
    document.getElementById('panelShelf').classList.toggle('hidden', isReviews);

    setTabActive('tabReviewsBtn', isReviews);
    setTabActive('tabShelfBtn', !isReviews);
}

function setTabActive(btnId, active) {
    const btn = document.getElementById(btnId);
    if (!btn) return;
    btn.classList.toggle('text-stone-800', active);
    btn.classList.toggle('border-amber-500', active);
    btn.classList.toggle('text-stone-400', !active);
    btn.classList.toggle('border-transparent', !active);
}

// ── 렌더링 ────────────────────────────────────────────────
function renderShelf() {
    const total = bsReading.length + bsFinished.length;
    const empty = document.getElementById('shelfEmpty');
    const calendar = document.getElementById('bookshelfCalendar');

    empty.classList.toggle('hidden', total > 0);
    calendar.classList.toggle('hidden', total === 0);

    renderReadingStrip();
    if (total > 0) renderCalendar();
}

function renderReadingStrip() {
    const strip = document.getElementById('readingStrip');
    const list = document.getElementById('readingStripList');
    if (bsReading.length === 0) {
        strip.classList.add('hidden');
        return;
    }
    strip.classList.remove('hidden');
    list.innerHTML = '';
    bsReading.forEach(entry => {
        const card = document.createElement('button');
        card.type = 'button';
        card.className = 'flex-shrink-0 w-24 text-left group';
        card.onclick = () => openEntryDetail(entry);
        card.innerHTML = `
            <div class="w-24 h-32 rounded-lg overflow-hidden shadow bg-stone-100 mb-1">
                ${thumbHtml(entry, 'w-full h-full object-cover group-hover:scale-105 transition-transform')}
            </div>
            <div class="text-xs font-semibold text-stone-700 line-clamp-2">${escapeHtml(entry.bookTitle)}</div>
            <div class="text-[11px] text-stone-400">${entry.startedAt ? shortDate(entry.startedAt) + '~' : ''}</div>
        `;
        list.appendChild(card);
    });
}

function renderCalendar() {
    document.getElementById('calendarLabel').textContent = `${calYear}년 ${calMonth + 1}월`;
    const grid = document.getElementById('calendarGrid');
    grid.innerHTML = '';

    const firstWeekday = new Date(calYear, calMonth, 1).getDay(); // 0=일
    const daysInMonth = new Date(calYear, calMonth + 1, 0).getDate();

    // 앞쪽 빈 칸
    for (let i = 0; i < firstWeekday; i++) {
        grid.appendChild(emptyCell());
    }

    for (let day = 1; day <= daysInMonth; day++) {
        const dateKey = `${calYear}-${pad(calMonth + 1)}-${pad(day)}`;
        const entries = bsFinishedByDate[dateKey] || [];
        grid.appendChild(dayCell(day, entries));
    }
}

function emptyCell() {
    const cell = document.createElement('div');
    cell.className = 'aspect-square';
    return cell;
}

function dayCell(day, entries) {
    const cell = document.createElement('div');
    cell.className = 'aspect-square border border-stone-100 rounded-lg p-0.5 flex flex-col overflow-hidden';

    const num = document.createElement('div');
    num.className = 'text-[10px] text-stone-400 leading-none px-0.5';
    num.textContent = day;
    cell.appendChild(num);

    if (entries.length > 0) {
        const books = document.createElement('div');
        books.className = 'flex-1 flex flex-wrap gap-0.5 items-center justify-center min-h-0';
        entries.forEach(entry => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.title = entry.bookTitle;
            btn.className = 'block h-full min-w-0 rounded overflow-hidden shadow-sm hover:ring-2 hover:ring-amber-400';
            btn.style.flex = `1 1 ${Math.max(30, Math.floor(100 / entries.length))}%`;
            btn.onclick = () => openEntryDetail(entry);
            btn.innerHTML = thumbHtml(entry, 'w-full h-full object-cover');
            books.appendChild(btn);
        });
        cell.appendChild(books);
    }
    return cell;
}

function bookshelfChangeMonth(delta) {
    calMonth += delta;
    if (calMonth < 0) { calMonth = 11; calYear--; }
    else if (calMonth > 11) { calMonth = 0; calYear++; }
    renderCalendar();
}

// ── 항목 상세/편집 ─────────────────────────────────────────
function openEntryDetail(entry) {
    bsEditingEntry = entry;
    document.getElementById('edTitle').textContent = entry.bookTitle;
    document.getElementById('edAuthor').textContent = entry.bookAuthor;
    setThumbnail('edThumbnail', entry);

    const ownerControls = document.getElementById('edOwnerControls');
    const readonly = document.getElementById('edReadonly');

    if (bsIsOwner) {
        readonly.classList.add('hidden');
        ownerControls.classList.remove('hidden');
        document.getElementById('edStartedAt').value = entry.startedAt || '';
        document.getElementById('edFinishedAt').value = entry.finishedAt || '';
    } else {
        ownerControls.classList.add('hidden');
        readonly.classList.remove('hidden');
        const started = entry.startedAt ? shortDate(entry.startedAt) : '―';
        const finished = entry.finishedAt ? shortDate(entry.finishedAt) : '읽는 중';
        readonly.innerHTML = `시작 ${started} · 완독 ${finished}`;
    }
    Modal.open('entryDetailModal');
}

function closeEntryDetailModal() {
    Modal.close('entryDetailModal');
    bsEditingEntry = null;
}

async function bsSaveEntryEdit() {
    if (!bsEditingEntry) return;
    const startedAt = document.getElementById('edStartedAt').value || null;
    const finishedAt = document.getElementById('edFinishedAt').value || null;
    if (startedAt && finishedAt && finishedAt < startedAt) {
        Alert.error('완독일은 시작일보다 앞설 수 없습니다.');
        return;
    }
    try {
        await API.put(`/api/bookshelf/${bsEditingEntry.id}`, { startedAt, finishedAt });
        location.reload();
    } catch (e) {
        Alert.error(e.message);
    }
}

async function bsDeleteEntry() {
    if (!bsEditingEntry) return;
    if (!Alert.confirm('이 책을 책장에서 삭제할까요?')) return;
    try {
        await API.delete(`/api/bookshelf/${bsEditingEntry.id}`);
        location.reload();
    } catch (e) {
        Alert.error(e.message);
    }
}

// ── 책 추가 모달 ───────────────────────────────────────────
function openAddBookModal() {
    bsClearSelection();
    document.getElementById('bsBookList').innerHTML = '';
    document.getElementById('bsBookSearch').value = '';
    Modal.open('addBookModal');
    setTimeout(() => document.getElementById('bsBookSearch').focus(), 50);
}

function closeAddBookModal() {
    Modal.close('addBookModal');
}

async function bsSearchBooks() {
    const query = document.getElementById('bsBookSearch').value.trim();
    if (!query) {
        Alert.error('검색어를 입력하세요');
        return;
    }
    const list = document.getElementById('bsBookList');
    const loading = document.getElementById('bsBookLoading');
    list.innerHTML = '';
    loading.classList.remove('hidden');

    try {
        const data = await API.get(`/api/external/books/search?query=${encodeURIComponent(query)}&page=1&size=12`);
        loading.classList.add('hidden');
        const docs = (data && data.documents) || [];
        if (docs.length === 0) {
            list.innerHTML = '<p class="text-stone-500 col-span-full text-center py-4">검색 결과가 없습니다.</p>';
            return;
        }
        docs.forEach(book => {
            const item = document.createElement('div');
            item.className = 'flex flex-col p-3 border-2 border-stone-200 rounded-lg cursor-pointer transition-all hover:border-amber-500 hover:bg-amber-50';
            item.onclick = () => bsSelectBook(book);
            item.innerHTML = `
                <img src="${book.thumbnail}" alt="${escapeHtml(book.title)}" class="w-full h-40 object-contain rounded mb-2">
                <div class="font-semibold text-sm mb-0.5 line-clamp-2">${escapeHtml(book.title)}</div>
                <div class="text-stone-500 text-xs line-clamp-1">${escapeHtml((book.authors || []).join(', '))}</div>
            `;
            list.appendChild(item);
        });
    } catch (e) {
        loading.classList.add('hidden');
        list.innerHTML = '<p class="text-red-500 col-span-full text-center py-4">검색에 실패했습니다.</p>';
    }
}

function bsSelectBook(book) {
    bsSelectedBook = {
        bookTitle: book.title,
        bookAuthor: (book.authors || []).join(', '),
        bookIsbn: book.isbn || '',
        bookThumbnail: book.thumbnail || null,
    };
    document.getElementById('bsSelectedTitle').textContent = bsSelectedBook.bookTitle;
    document.getElementById('bsSelectedAuthor').textContent = bsSelectedBook.bookAuthor;
    document.getElementById('bsSelectedThumbnail').src = bsSelectedBook.bookThumbnail || '';
    document.getElementById('bsSearchSection').classList.add('hidden');
    document.getElementById('bsSelectedSection').classList.remove('hidden');
}

function bsClearSelection() {
    bsSelectedBook = null;
    document.getElementById('bsStartedAt').value = '';
    document.getElementById('bsFinishedAt').value = '';
    document.getElementById('bsSelectedSection').classList.add('hidden');
    document.getElementById('bsSearchSection').classList.remove('hidden');
}

async function bsSubmitEntry() {
    if (!bsSelectedBook) return;
    const startedAt = document.getElementById('bsStartedAt').value || null;
    const finishedAt = document.getElementById('bsFinishedAt').value || null;
    if (startedAt && finishedAt && finishedAt < startedAt) {
        Alert.error('완독일은 시작일보다 앞설 수 없습니다.');
        return;
    }
    try {
        await API.post('/api/bookshelf', { ...bsSelectedBook, startedAt, finishedAt });
        location.reload();
    } catch (e) {
        Alert.error(e.message);
    }
}

// ── 헬퍼 ─────────────────────────────────────────────────
function setThumbnail(imgId, entry) {
    const img = document.getElementById(imgId);
    if (entry.bookThumbnail) {
        img.src = entry.bookThumbnail;
        img.classList.remove('hidden');
    } else {
        img.removeAttribute('src');
        img.classList.add('hidden');
    }
}

function thumbHtml(entry, imgClass) {
    if (entry.bookThumbnail) {
        return `<img src="${entry.bookThumbnail}" alt="${escapeHtml(entry.bookTitle)}" class="${imgClass}">`;
    }
    return `<div class="w-full h-full flex items-center justify-center bg-stone-200 text-stone-400 text-lg">📖</div>`;
}

function pad(n) {
    return String(n).padStart(2, '0');
}

function shortDate(isoDate) {
    // 'YYYY-MM-DD' -> 'YYYY.MM.DD'
    return isoDate.replace(/-/g, '.');
}

function escapeHtml(str) {
    return String(str == null ? '' : str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}
