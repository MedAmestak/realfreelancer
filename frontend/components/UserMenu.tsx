import { Menu, MenuItemProps, Transition } from '@headlessui/react';
import { Fragment } from 'react';
import { ChevronDownIcon, UserCircleIcon } from '@heroicons/react/20/solid';
import Link from 'next/link';
import { useAuth } from '../src/contexts/AuthContext'; // Adjust path as needed
import { User, LogOut, Settings, ScatterChart, LayoutDashboardIcon, BookMarkedIcon, BookMarked, LucideBookMarked, BookCheckIcon, LucidePodcast, PoundSterling, BookmarkPlus, PanelTopCloseIcon, PanelTop } from 'lucide-react';
import { useRouter } from 'next/navigation';

export default function UserMenu() {
  const { user, logout } = useAuth();
  const router = useRouter();

  if (!user) {
    return null;
  }

  return (
    <div className="relative inline-block text-left">
      <Menu as={'div' as const}>
        <div>
          <Menu.Button className="inline-flex w-full justify-center rounded-full text-sm font-medium text-white hover:bg-opacity-30 focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75">
            <UserCircleIcon
              className="h-10 w-10 text-gray-500 hover:text-primary-600"
              aria-hidden="true"
            />
          </Menu.Button>
        </div>
        <Transition
          as={Fragment}
          enter="transition ease-out duration-100"
          enterFrom="transform opacity-0 scale-95"
          enterTo="transform opacity-100 scale-100"
          leave="transition ease-in duration-75"
          leaveFrom="transform opacity-100 scale-100"
          leaveTo="transform opacity-0 scale-95"
        >
          <Menu.Items className="absolute right-0 mt-2 w-56 origin-top-right divide-y divide-gray-100 rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
            <div className="px-1 py-1 ">
                <div className="px-4 py-2 border-b border-gray-200">
                    <p className="text-sm text-gray-900">{user.username}</p>
                </div>
                <Menu.Item>
                {({ active }: { active: boolean }) => (
                  <Link
                    href="/"
                    className={`${
                      active ? 'bg-primary-500 text-white' : 'text-gray-900'
                    } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                  >
                    <PanelTop className="mr-2 h-5 w-5" aria-hidden="true" />
                    Home
                  </Link>
                )}
                </Menu.Item>
                <Menu.Item>
                {({ active }: { active: boolean }) => (
                  <Link
                    href="/dashboard"
                    className={`${
                      active ? 'bg-primary-500 text-white' : 'text-gray-900'
                    } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                  >
                    <LayoutDashboardIcon className="mr-2 h-5 w-5" aria-hidden="true" />
                    Dashboard
                  </Link>
                )}
              </Menu.Item>
              <Menu.Item>
                {({ active }: { active: boolean }) => (
                  <Link
                    href="/post"
                    className={`${
                      active ? 'bg-primary-500 text-white' : 'text-gray-900'
                    } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                  >
                    <BookmarkPlus className="mr-2 h-5 w-5" aria-hidden="true" />
                    Post New Project
                  </Link>
                )}
              </Menu.Item>
              <Menu.Item>
                {({ active }: { active: boolean }) => (
                  <Link
                    href="/profile"
                    className={`${
                      active ? 'bg-primary-500 text-white' : 'text-gray-900'
                    } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                  >
                    <User className="mr-2 h-5 w-5" aria-hidden="true" />
                    Profile
                  </Link>
                )}
              </Menu.Item>
              <Menu.Item>
                {({ active }: { active: boolean }) => (
                  <button
                    className={`${
                      active ? 'bg-primary-500 text-white' : 'text-gray-900'
                    } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                  >
                    <Settings className="mr-2 h-5 w-5" aria-hidden="true" />
                    Settings
                  </button>
                )}
              </Menu.Item>
            </div>
            <div className="px-1 py-1">
              <Menu.Item>
                {({ active }: { active: boolean }) => (
                  <button
                    onClick={() => { logout(); router.push('/'); }}
                    className={`${
                      active ? 'bg-red-500 text-white' : 'text-gray-900'
                    } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                  >
                    <LogOut className="mr-2 h-5 w-5" aria-hidden="true" />
                    Logout
                  </button>
                )}
              </Menu.Item>
            </div>
          </Menu.Items>
        </Transition>
      </Menu>
    </div>
  );
} 