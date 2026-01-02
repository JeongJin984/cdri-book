/*
[문학] 너에게 해주지 못한 말들, 권태영
[문학] 단순하게 배부르게, 현영서
[문학] 게으른 사랑, 권태영
[경제경영] 트랜드 코리아 2322, 권태영
[경제경영] 초격자 투자, 장동혁
[경제경영] 파이어족 강환국의 하면 되지 않는다! 퀀트 투자, 홍길동
[인문학] 진심보다 밥, 이서연
[인문학] 실패에 대하여 생각하지 마라, 위성원
[IT] 실리콘밸리 리더십 쉽다, 지승열
[IT] 데이터분석을 위한 A 프로그래밍, 지승열
[IT] 인공지능1-12, 장동혁
[IT] -1년차 게임 개발, 위성원
[IT] Skye가 알려주는 피부 채색의 비결, 권태영
[과학] 자연의 발전, 장지명
[과학] 코스모스 필 무렵, 이승열
 */

insert into category (id, name, status) values (1, '문학', 'OK');
insert into category (id, name, status) values (2, '경제경영', 'OK');
insert into category (id, name, status) values (3, '인문학', 'OK');
insert into category (id, name, status) values (4, 'IT', 'OK');
insert into category (id, name, status) values (5, '과학', 'OK');

insert into book (id, title, author, status) values (1, '너에게 해주지 못한 말들', '권태영', 'OK');
insert into book (id, title, author, status) values (2, '단순하게 배부르게', '현영서', 'OK');
insert into book (id, title, author, status) values (3, '게으른 사랑', '권태영', 'OK');
insert into book (id, title, author, status) values (4, '트랜드 코리아 2322', '권태영', 'OK');
insert into book (id, title, author, status) values (5, '초격자 투자', '장동혁', 'OK');
insert into book (id, title, author, status) values (6, '파이어족 강환국의 하면 되지 않는다! 퀀트 투자', '홍길동', 'OK');
insert into book (id, title, author, status) values (7, '진심보다 밥', '이서연', 'OK');
insert into book (id, title, author, status) values (8, '실패에 대하여 생각하지 마라', '위성원', 'OK');
insert into book (id, title, author, status) values (9, '실리콘밸리 리더십 쉽다', '지승열', 'OK');
insert into book (id, title, author, status) values (10, '데이터분석을 위한 A 프로그래밍', '지승열', 'OK');
insert into book (id, title, author, status) values (11, '인공지능1-12', '장동혁', 'OK');
insert into book (id, title, author, status) values (12, '-1년차 게임 개발', '위성원', 'OK');
insert into book (id, title, author, status) values (13, 'Skye가 알려주는 피부 채색의 비결', '권태영', 'OK');
insert into book (id, title, author, status) values (14, '자연의 발전', '장지명', 'OK');
insert into book (id, title, author, status) values (15, '코스모스 필 무렵', '이승열', 'OK');

insert into book_category_map (id, book_id, category_id) values (1, 1, 1);
insert into book_category_map (id, book_id, category_id) values (2, 2, 1);
insert into book_category_map (id, book_id, category_id) values (3, 3, 1);
insert into book_category_map (id, book_id, category_id) values (4, 4, 2);
insert into book_category_map (id, book_id, category_id) values (5, 5, 2);
insert into book_category_map (id, book_id, category_id) values (6, 6, 2);
insert into book_category_map (id, book_id, category_id) values (7, 7, 3);
insert into book_category_map (id, book_id, category_id) values (8, 8, 3);
insert into book_category_map (id, book_id, category_id) values (9, 9, 4);
insert into book_category_map (id, book_id, category_id) values (10, 10, 4);
insert into book_category_map (id, book_id, category_id) values (11, 11, 4);
insert into book_category_map (id, book_id, category_id) values (12, 12, 4);
insert into book_category_map (id, book_id, category_id) values (13, 13, 4);
insert into book_category_map (id, book_id, category_id) values (14, 14, 5);
insert into book_category_map (id, book_id, category_id) values (15, 15, 5);
