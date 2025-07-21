-- Bước 1: Thêm cột cho phép null
ALTER TABLE playlists
ADD playlist_url VARCHAR(255);

-- Bước 2: Cập nhật dữ liệu tạm
UPDATE playlists
SET playlist_url = 'https://i1.sndcdn.com/avatars-pXAmgKVNbQvrXCST-MAaq0g-t240x240.jpg'  -- Hoặc bất kỳ giá trị mặc định nào bạn muốn
WHERE playlist_url IS NULL