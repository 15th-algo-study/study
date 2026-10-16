import { TOTAL_DAYS } from './config.js';

function addDays(iso, n) {
  const [y, m, d] = iso.split('-').map(Number);
  return new Date(Date.UTC(y, m - 1, d + n)).toISOString().slice(0, 10);
}

/** 지정한 시간대의 오늘 날짜 'YYYY-MM-DD'. */
export function todayIn(timezone) {
  return new Intl.DateTimeFormat('en-CA', { timeZone: timezone, year: 'numeric', month: '2-digit', day: '2-digit' })
    .format(new Date());
}

/** 1일차부터 TOTAL_DAYS 일차까지의 날짜. holidays 만 건너뛴다 — 주말 포함 매일. (scripts/attendance.py 와 같은 규칙) */
export function studyDates(startDate, holidays = []) {
  const skip = new Set(holidays);
  const dates = [];
  let d = startDate;
  while (dates.length < TOTAL_DAYS) {
    if (!skip.has(d)) dates.push(d);
    d = addDays(d, 1);
  }
  return dates;
}

/** 오늘 기준 해금된 마지막 일차 (시작 전 0). */
export function schedule(cfg) {
  const today = todayIn(cfg.timezone);
  if (!cfg.start_date) return { today, todayDay: 0, dates: [], started: false };
  const dates = studyDates(cfg.start_date, cfg.holidays);
  const todayDay = dates.filter((x) => x <= today).length;
  return { today, todayDay, dates, started: true };
}
