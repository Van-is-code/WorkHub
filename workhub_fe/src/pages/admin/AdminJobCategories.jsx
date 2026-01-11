import React, { useEffect, useState } from 'react';
import { getJobCategories, createJobCategory, updateJobCategory, deleteJobCategory } from '../../apiService';
import { Squares2X2Icon, PencilSquareIcon, TrashIcon, XMarkIcon, CheckIcon, PlusIcon } from '@heroicons/react/24/outline';

const AdminJobCategories = () => {
  const [categories, setCategories] = useState([]);
  const [newCategory, setNewCategory] = useState('');
  const [editId, setEditId] = useState(null);
  const [editValue, setEditValue] = useState('');

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    const res = await getJobCategories();
    setCategories(res.data);
  };

  const handleAdd = async () => {
    if (!newCategory.trim()) return;
    await createJobCategory({ name: newCategory });
    setNewCategory('');
    fetchCategories();
  };

  const handleEdit = (id, name) => {
    setEditId(id);
    setEditValue(name);
  };

  const handleUpdate = async (id) => {
    await updateJobCategory(id, { name: editValue });
    setEditId(null);
    setEditValue('');
    fetchCategories();
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc muốn xóa?')) {
      await deleteJobCategory(id);
      fetchCategories();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-3xl mx-auto">
        <div className="flex items-center gap-2 mb-8">
          <Squares2X2Icon className="w-8 h-8 text-primary" />
          <h2 className="text-3xl font-extrabold text-blue-700">Quản lý Danh Mục Công Việc</h2>
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-8">
          <div className="flex mb-6 gap-2">
            <input
              className="border px-4 py-2 flex-1 rounded-l"
              value={newCategory}
              onChange={e => setNewCategory(e.target.value)}
              placeholder="Tên danh mục mới"
            />
            <button className="bg-primary text-white px-5 py-2 rounded-r flex items-center gap-1 font-semibold" onClick={handleAdd} type="button">
              <PlusIcon className="w-5 h-5" /> Thêm
            </button>
          </div>
          <table className="min-w-full divide-y divide-gray-200">
            <thead>
              <tr className="bg-blue-50">
                <th className="px-4 py-3 text-left font-bold text-gray-700">ID</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Tên danh mục</th>
                <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {categories.map(cat => (
                <tr key={cat.id} className="hover:bg-blue-50 transition">
                  <td className="px-4 py-2">{cat.id}</td>
                  <td className="px-4 py-2">
                    {editId === cat.id ? (
                      <input
                        value={editValue}
                        onChange={e => setEditValue(e.target.value)}
                        className="border px-2 py-1 rounded"
                      />
                    ) : (
                      cat.name
                    )}
                  </td>
                  <td className="px-4 py-2 flex gap-2 justify-center">
                    {editId === cat.id ? (
                      <>
                        <button className="p-2 rounded hover:bg-green-100" onClick={() => handleUpdate(cat.id)} title="Lưu" type="button">
                          <CheckIcon className="w-5 h-5 text-green-600" />
                        </button>
                        <button className="p-2 rounded hover:bg-gray-100" onClick={() => setEditId(null)} title="Hủy" type="button">
                          <XMarkIcon className="w-5 h-5 text-gray-500" />
                        </button>
                      </>
                    ) : (
                      <>
                        <button className="p-2 rounded hover:bg-yellow-100" onClick={() => handleEdit(cat.id, cat.name)} title="Sửa" type="button">
                          <PencilSquareIcon className="w-5 h-5 text-yellow-600" />
                        </button>
                        <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDelete(cat.id)} title="Xóa" type="button">
                          <TrashIcon className="w-5 h-5 text-red-600" />
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AdminJobCategories;
